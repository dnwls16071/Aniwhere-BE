import time
import requests
from openai import OpenAI
import mysql.connector

# GraphQL 엔드포인트 및 쿼리
anilist_url = 'https://graphql.anilist.co'

query = '''
query ($page: Int) { 
  Page(page: $page, perPage: 1) { 
    media(seasonYear: 2024) { 
      id 
      title { 
        romaji 
      }
      staff { 
        edges { 
          node { 
            name { 
              full 
            } 
          } 
          role 
        } 
      } 
    } 
  } 
}
'''

# 제작진 직책 번역 테이블
role_translation = {
    "Director": "감독",
    "Character Design": "캐릭터 디자인",
    "Animation Producer": "애니메이션 프로듀서",
    "Chief Animation Director": "작화감독",
    "Script": "각본",
    "Music": "음악"
}


def translate_with_chatgpt(name):
    try:
        client = OpenAI(
            api_key='API'
        )  # API 키 설정

        response = client.chat.completions.create(
            model="gpt-4",
            messages=[
                {"role": "system", "content": "일본인 이름인데 한국말로 바꿔줘. 추가설명이랑 URL은 없어도 괜찮아."},
                {"role": "user", "content": name}
            ]
        )

        translated_text = response.choices[0].message.content.strip()
        return translated_text

    except Exception as e:
        print(f"Error during translation: {e}")
        return



def process_staff(media, anime_id):
    connection = mysql.connector.connect(
        host="localhost",  # MySQL 호스트
        user="root",  # MySQL 사용자 이름
        password="1234",  # MySQL 비밀번호
        database="anime_db"  # 사용할 데이터베이스
    )
    cursor = connection.cursor()

    for staff_member in media['staff']['edges']:
        role = staff_member['role']


        if role in role_translation:
            original_name = staff_member['node']['name']['full']
            translated_name = translate_with_chatgpt(original_name)  # GPT로 이름 번역
            translated_role = role_translation[role]  # DB 컬럼명으로 변환된 직책


            update_query = f"UPDATE anime SET {translated_role} = %s WHERE anilist_id = %s"


            cursor.execute(update_query, (translated_name, anime_id))
            print(f"Updated {translated_role} with {translated_name} for anime ID {anime_id}")


for page_number in range(1, 30):
    time.sleep(1)
    response = requests.post(anilist_url, json={'query': query, 'variables': {'page': page_number}})

    if response.status_code == 200:
        data = response.json()

        media_list = data['data']['Page']['media']

        for media in media_list:
            anime_id = media['id']

            process_staff(media, anime_id)

    else:
        print(f"Error: {response.status_code}, {response.text}")
        continue
