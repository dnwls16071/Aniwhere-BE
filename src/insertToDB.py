import time

import mysql.connector
import requests
from bs4 import BeautifulSoup
from datetime import date

from mysql.connector import cursor

# 장르 번역
genre_translation = {
    "Action": "액션", "Adventure": "모험", "Comedy": "코미디", "Drama": "드라마",
    "Fantasy": "판타지", "Horror": "공포", "Mystery": "미스터리", "Romance": "로맨스",
    "Sci-Fi": "SF", "Slice of Life": "일상", "Sports": "스포츠", "Supernatural": "초능력",
    "Thriller": "스릴러", "Ecchi": "에치", "Mahou Shoujo": "마법소녀", "Mecha": "메카",
    "Music": "음악", "Psychological": "심리", "Hentai": "야애니"
}

# 방영중 여부
running_ = {
    "FINISHED": "방영종료", "RELEASING": "방영중"
}

# 제작진 번역
role_translation = {
    "Director": "감독", "Character Design": "캐릭터 디자인", "Animation Producer": "애니메이션 프로듀서",
    "Chief Animation Director": "작화감독", "Script": "각본", "Music": "음악"
}

weekday_converter = {
    0: "월요일", 1: "화요일", 2: "수요일", 3: "목요일", 4: "금요일", 5: "토요일", 6: "일요일"
}



def translate_genres(genres):
    return [genre_translation.get(genre, genre) for genre in genres]



def translate_status(status):
    return running_.get(status, status)



def get_weekday_from_date(start_date):
    try:
        start_date_obj = date(start_date['year'], start_date['month'], start_date['day'])
        weekday = start_date_obj.weekday()
        return weekday_converter[weekday]
    except Exception as e:
        return "Unknown"

def calculate_quarter(start_date):
    month = start_date['month']
    if not month: return None
    if 1 <= month <= 3:
        return 1  # 1분기
    elif 4 <= month <= 6:
        return 2  # 2분기
    elif 7 <= month <= 9:
        return 3  # 3분기
    else:
        return 4  # 4분기


def insert_into_db(anime_data):
    global connection
    try:
        # MySQL 연결 설정
        connection = mysql.connector.connect(
            host="localhost",  # MySQL 호스트
            user="root",  # MySQL 사용자 이름
            password="1234",  # MySQL 비밀번호
            database="anime_db"  # 사용할 데이터베이스
        )
        cursor = connection.cursor()


        insert_query = """
        INSERT INTO anime (release_date, end_date, episodes, duration, is_adult, status, poster, trailer, studio, weekday, airing_quarter, anilist_id)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """


        cursor.executemany(insert_query, anime_data)
        connection.commit()  # 커밋하여 변경사항 저장
        print(f"{cursor.rowcount} rows inserted successfully.")

    except mysql.connector.Error as err:
        print(f"Error: {err}")

    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

def get_or_insert_category(cursor, genre):

    select_query = "SELECT category_id FROM categories WHERE category_name = %s"
    cursor.execute(select_query, (genre,))
    result = cursor.fetchone()


    if result:
        return result[0]
    else:

        insert_query = "INSERT INTO categories (category_name) VALUES (%s)"
        cursor.execute(insert_query, (genre,))
        return cursor.lastrowid

def insert_anime_categories(anime_id, genres):
    connection.cursor()
    for genre in genres:
        category_id = get_or_insert_category(cursor, genre)
        insert_query = "INSERT INTO animecategories (anime_id, category_id) VALUES (%s, %s)"
        cursor.execute(insert_query, (anime_id, category_id))

anilist_url = 'https://graphql.anilist.co'

query = '''
query ($page: Int) { 
  Page(page: $page, perPage: 1) { 
    media(seasonYear: 2024) { 
      id 
      title { 
        romaji 
        english 
        native 
      } 
      description 
      startDate { 
        year 
        month 
        day 
      } 
      endDate { 
        year 
        month 
        day 
      } 
      season 
      seasonYear 
      episodes 
      duration 
      genres 
      isAdult 
      status 
      coverImage { 
        large 
      } 
      trailer { 
        id 
        site 
        thumbnail 
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
      studios { 
        edges { 
          node { 
            name 
          } 
        } 
      } 
    } 
  } 
}
'''


def chkEndDate(end_date):
    if end_date and end_date.get('year') and end_date.get('month') and end_date.get('day'):
        return end_date
    else:
        return None

def chkstartDate(start_date):
    if start_date and start_date.get('day') == None:
        start_date['day'] = 1
        return start_date
    elif start_date and start_date.get('month') == None or start_date.get('year') == None:
        return None
    else:
        return start_date


for page_number in range(1, 20):
    time.sleep(1)
    response = requests.post(anilist_url, json={'query': query, 'variables': {'page': page_number}})

    if response.status_code == 200:
        data = response.json()
    else:
        print(f"Error: {response.status_code}, {response.text}")
        continue

    if data:

        anime_list = []

        for index, anime in enumerate(data['data']['Page']['media'], start=1):  # 순차적으로 id를 설정
            description_clean = BeautifulSoup(anime['description'], 'html.parser').get_text() if anime[
                'description'] else "No description available"

            studios = [studio['node']['name'] for studio in anime['studios']['edges'][:3]]  # 최대 3개의 제작사만 가져옴
            translated_genres = translate_genres(anime['genres'])
            weekday = get_weekday_from_date(anime['startDate'])

            translated_status = translate_status(anime['status'])

            insert_anime_categories(anime['id'], translated_genres)

            end_date = chkEndDate(anime['endDate'])
            start_date = chkstartDate(anime['startDate'])

            quarter = calculate_quarter(start_date)

            anime_info = {
                'start_date': start_date,
                'end_date': end_date,
                'episodes': anime.get('episodes'),
                'duration': anime.get('duration'),
                'isAdult': anime['isAdult'],
                'status':  translated_status,
                'cover_image': anime['coverImage']['large'],
                'trailer': anime['trailer'],
                'studios': studios,
                'weekday': weekday,
                'quarter' : quarter
            }

            anime_list.append(anime_info)

            # MySQL에 맞게 데이터 포맷팅
        anime_data = [
            (
                f"{anime['start_date']['year']}-{anime['start_date']['month']}-{anime['start_date']['day']}",
                f"{anime['end_date']['year']}-{anime['end_date']['month']}-{anime['end_date']['day']}" if anime[
                    'end_date'] else None,
                anime.get('episodes'),
                anime.get('duration'),
                anime['isAdult'],
                anime['status'],
                anime['cover_image'],
                f"https://www.youtube.com/watch?v={anime['trailer']['id']}" if anime['trailer'] and
                                                                               anime['trailer'][
                                                                                   'site'].lower() == "youtube" else None,
                ', '.join(anime['studios']),
                anime['weekday'],
                anime['quarter'],
                anime['id']
            )
            for anime in anime_list
        ]

        insert_into_db(anime_data)


        print(f"Data from page {page_number} processed.")
    else:
        print(f"No data found on page {page_number}")