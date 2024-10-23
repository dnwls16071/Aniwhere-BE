import time

import mysql.connector
import requests
from openai import OpenAI


def update_title(title, id):
    try:
        connection = mysql.connector.connect(
            host="localhost",  # MySQL 호스트
            user="root",  # MySQL 사용자 이름
            password="1234",  # MySQL 비밀번호
            database="anime_db"  # 사용할 데이터베이스
        )
        cursor = connection.cursor()


        insert_query = """
                UPDATE anime 
                SET title = %s 
                WHERE anilist_id = %s
        """

        cursor.executemany(insert_query, (title, id))
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
    } 
  } 
}
'''

def translate_with_chatgpt(text):
    try:
        client = OpenAI(
            api_key='API')  # API 키

        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "이 애니메이션, 한국어 제목을 찾아서 알려줘. 추가설명은 필요없고 제목만 알려줘"},
                {"role": "user", "content": text}
            ]
        )

        translated_text = response.choices[0].message.content.strip()
        return translated_text

    except Exception as e:
        print(f"Error during translation: {e}")
        return text

for page_number in range(1, 20):
    time.sleep(1)
    response = requests.post(anilist_url, json={'query': query, 'variables': {'page': page_number}})

    if response.status_code == 200:
        data = response.json()

        media_list = data['data']['Page']['media']

        for media in media_list:
            anime_id = media['id']
            anime_romaji = media['title']['romaji']

            title = translate_with_chatgpt(anime_romaji)
            update_title(title, anime_id)

    else:
        print(f"Error: {response.status_code}, {response.text}")
        continue