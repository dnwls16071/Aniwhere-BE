import time

import mysql.connector
import requests
from openai import OpenAI

connection = mysql.connector.connect(
    host="localhost",  # MySQL 호스트
    user="root",  # MySQL 사용자 이름
    password="1234",  # MySQL 비밀번호
    database="anime_db"  # 사용할 데이터베이스
)
cursor = connection.cursor()

def get_or_insert_voice_actor(voice_actor_name):
    select_query = "SELECT voice_actor_id FROM voiceactors WHERE name = %s"
    cursor.execute(select_query, (voice_actor_name,))
    result = cursor.fetchone()

    if result:
        return result[0]
    else:
        insert_query = "INSERT INTO voiceactors (name) VALUES (%s)"
        cursor.execute(insert_query, (voice_actor_name,))
        connection.commit()
        return cursor.lastrowid

def update_title(dic):
    try:
        if dic[0][3] is None:
            update_query = """
                INSERT INTO casting (anime_id, character_name, character_description)
                VALUES (%s, %s, %s)
            """
            cursor.execute(update_query, dic[0][:3])
        else:
            voice_actor_id = get_or_insert_voice_actor(dic[0][3])
            update_query = """
                INSERT INTO casting (anime_id, character_name, character_description, voice_actor_id)
                VALUES (%s, %s, %s, %s)
            """
            cursor.execute(update_query, (dic[0][0], dic[0][1], dic[0][2], voice_actor_id))

        connection.commit()
        print(f"{cursor.rowcount} rows inserted successfully.")

    except mysql.connector.Error as err:
        print(f"Error: {err}")


anilist_url = 'https://graphql.anilist.co'

query = '''
query ($page: Int) { 
  Page(page: $page, perPage: 1) { 
    media(seasonYear: 2024) { 
      id 
      title { 
        romaji 
      }
      characters { 
        edges { 
          node { 
            name { 
              full 
            } 
            description 
          } 
          voiceActors(language: JAPANESE) { 
            name { 
              full 
            } 
          } 
        } 
      } 
    } 
  } 
}
'''

def translate_with_chatgpt(title, character):
    try:
        client = OpenAI(
            api_key='API')  # API 키

        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": title+"이라는 애니메이션에 나오는 등장인물인데, 참고해서 번역해줘.추가설명이랑 URL은 없어도 괜찮아."},
                {"role": "user", "content": character.get(character_name)}
            ]
        )

        translated_text = response.choices[0].message.content.strip()
        return translated_text

    except Exception as e:
        print(f"Error during translation: {e}")
        return

def translate_with_chatgpt_descript(title, character):
    try:
        client = OpenAI(
            api_key='API')  # API 키

        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": title+"이라는 애니메이션에 나오는 등장인물에 대한 설명인데, 참고해서 번역해줘.추가설명이랑 URL은 없어도 괜찮아."},
                {"role": "user", "content": character.get(character_description)}
            ]
        )

        translated_text = response.choices[0].message.content.strip()
        return translated_text

    except Exception as e:
        print(f"Error during translation: {e}")
        return

def translate_with_chatgpt_voiceActor(character):
    try:
        client = OpenAI(
            api_key='API')  # API 키

        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {"role": "system", "content": "성우 이름인데 한국말로 바꿔줘. 추가설명이랑 URL은 없어도 괜찮아."},
                {"role": "user", "content": character.get(voice_actor_name)}
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
            anime_id = media['id']
            anime_title = media['title']['romaji']

            for character in media['characters']['edges'][:5]:
                character_name = character['node']['name']['full']
                character_description = character['node']['description'] if character['node'][
                    'description'] else "No description"


                if character['voiceActors']:
                    voice_actor_name = character['voiceActors'][0]['name']['full']
                    voice_actor_name_translated = translate_with_chatgpt_voiceActor(voice_actor_name)
                else:
                    voice_actor_name_translated = None

                character_dic = [
                    (anime_id,
                     translate_with_chatgpt(anime_title, character_name),
                     translate_with_chatgpt_descript(anime_title, character_description),
                     voice_actor_name_translated)
                ]

                update_title(character_dic)

    else:
        print(f"Error: {response.status_code}, {response.text}")
        continue

cursor.close()
connection.close()