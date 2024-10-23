import time

import mysql.connector
import requests
from openai import OpenAI


def update_title(title, id):
    try:
        # MySQL 연결 설정
        connection = mysql.connector.connect(
            host="localhost",  # MySQL 호스트
            user="root",  # MySQL 사용자 이름
            password="1234",  # MySQL 비밀번호
            database="anime_db"  # 사용할 데이터베이스
        )
        cursor = connection.cursor()

        update_query = """
                UPDATE anime 
                SET description = %s 
                WHERE anilist_id = %s
        """

        cursor.executemany(update_query, (title, id))
        connection.commit()
        print(f"{cursor.rowcount} rows inserted successfully.")

    except mysql.connector.Error as err:
        print(f"Error: {err}")

    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


anilist_url = 'https://graphql.anilist.co'

query = '''
query ($page: Int) { 
  Page(page: $page, perPage: 1) { 
    media(seasonYear: 2024) { 
      id 
      title { 
        romaji 
      }
      description
    } 
  } 
}
'''

def translate_with_chatgpt(title, description):
    try:
        client = OpenAI(
            api_key='API')  # API 키

        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": title+"이라는 애니메이션에 대한 줄거리인데, 참고해서 번역해줘.추가설명이랑 URL은 없어도 괜찮아."},
                {"role": "user", "content": description}
            ]
        )

        translated_text = response.choices[0].message.content.strip()
        return translated_text

    except Exception as e:
        print(f"Error during translation: {e}")
        return

for page_number in range(1, 30):
    time.sleep(1)
    response = requests.post(anilist_url, json={'query': query, 'variables': {'page': page_number}})

    if response.status_code == 200:
        data = response.json()

        media_list = data['data']['Page']['media']

        for media in media_list:
            anime_id = media['id']  # id 추출
            anime_romaji = media['title']['romaji']
            description = media['description']

            text = translate_with_chatgpt(anime_romaji, description)
            update_title(text, anime_id)

    else:
        print(f"Error: {response.status_code}, {response.text}")
        continue