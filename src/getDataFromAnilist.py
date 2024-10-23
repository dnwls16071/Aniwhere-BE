import time
import requests
from bs4 import BeautifulSoup
from datetime import date

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


def keep_original_text(text):
    return text

def translate_genres(genres):
    translated_genres = [genre_translation.get(genre, genre) for genre in genres]
    return translated_genres

def translate_role(role):
    return role_translation.get(role, role)

# 주요 제작진 필터링
def filter_important_staff(staff):
    filtered_staff = [{'name': staff_member['name'], 'role': translate_role(staff_member['role'])}
                      for staff_member in staff if staff_member['role'] in role_translation]
    return filtered_staff

def translate_status(status):
    return running_.get(status, status)

# 요일 표기
def get_weekday_from_date(start_date):
    try:
        start_date_obj = date(start_date['year'], start_date['month'], start_date['day'])
        weekday = start_date_obj.weekday()
        return weekday_converter[weekday]
    except Exception as e:
        return "Unknown"

anilist_url = 'https://graphql.anilist.co'

query = '''
query ($page: Int) { 
  Page(page: $page, perPage: 50) { 
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

for page_number in range(1, 10):
    response = requests.post(anilist_url, json={'query': query, 'variables': {'page': page_number}})

    if response.status_code == 200:
        data = response.json()
    else:
        print(f"Error: {response.status_code}, {response.text}")
        data = None

    anime_list = []

    if data:
        for anime in data['data']['Page']['media']:
            if anime['description']:
                description_clean = BeautifulSoup(anime['description'], 'html.parser').get_text()
            else:
                description_clean = "No description available"

            # 원래 텍스트를 그대로 사용
            original_title_romaji = keep_original_text(anime['title']['romaji'])
            original_title_english = keep_original_text(anime['title']['english'])
            original_description = keep_original_text(description_clean)

            characters = []
            for character in anime['characters']['edges'][:5]:
                character_info = {
                    'name': keep_original_text(character['node']['name']['full']),
                    'description': keep_original_text(character['node']['description'].strip() if character['node']['description'] else ""),
                    'voice_actors': [keep_original_text(va['name']['full']) for va in character.get('voiceActors', [])]
                }
                characters.append(character_info)

            staff = []
            for staff_member in anime['staff']['edges']:
                staff_info = {
                    'name': keep_original_text(staff_member['node']['name']['full']),
                    'role': staff_member.get('role', "No role available")
                }
                staff.append(staff_info)

            studios = [keep_original_text(studio['node']['name']) for studio in anime['studios']['edges'][:3]]  # 최대 3개의 제작사만 가져옴

            translated_genres = translate_genres(anime['genres'])

            important_staff = filter_important_staff(staff)

            weekday = get_weekday_from_date(anime['startDate'])

            anime_info = {
                'id': anime['id'],
                'title_romaji': original_title_romaji,
                'title_english': original_title_english,
                'title_native': anime['title']['native'],
                'description': original_description,
                'start_date': anime['startDate'],
                'end_date': anime['endDate'],
                'episodes': anime.get('episodes'),
                'duration': anime.get('duration'),
                'genres': translated_genres,
                'isAdult': anime['isAdult'],
                'status': anime['status'],
                'cover_image': anime['coverImage']['large'],
                'trailer': anime['trailer'],
                'characters': characters,
                'staff': important_staff,
                'studios': studios,
                'weekday': weekday
            }
            anime_list.append(anime_info)

    time.sleep(10)

    # 파일로 저장
    if anime_list:
        with open("anime_data.txt", "a", encoding="utf-8") as file:
            start_index = (page_number - 1) * 50 + 1
            for index, anime in enumerate(anime_list, start=start_index):
                file.write(f"Anime {index}:\n")
                file.write(f"ID: {anime['id']}\n")
                file.write(f"제목 (Romaji): {anime['title_romaji']}\n")
                file.write(f"제목 (English): {anime['title_english']}\n")
                file.write(f"제목 (원문): {anime['title_native']}\n")
                file.write(f"줄거리: {anime['description']}\n")
                file.write(
                    f"방영 시작일: {anime['start_date']['year']}-{anime['start_date']['month']}-{anime['start_date']['day']}\n")
                if anime['end_date']:
                    file.write(
                        f"방영 종료일: {anime['end_date']['year']}-{anime['end_date']['month']}-{anime['end_date']['day']}\n")
                else:
                    file.write("End Date: N/A\n")

                file.write(f"방영 요일: {anime['weekday']}\n")

                file.write(f"화수: {anime['episodes']}화\n")
                file.write(f"러닝타임: {anime['duration']}분\n")

                file.write(f"장르: {', '.join(anime['genres'])}\n")

                file.write(f"성인등급: {'O' if anime['isAdult'] else 'X'}\n")
                translated_status = translate_status(anime['status'])
                file.write(f"방영여부: {translated_status}\n")
                file.write(f"키 비주얼 URL: {anime['cover_image']}\n")
                if anime['trailer']:
                    if anime['trailer']['site'].lower() == "youtube":
                        trailer_url = f"https://www.youtube.com/watch?v={anime['trailer']['id']}"
                    else:
                        trailer_url = f"{anime['trailer']['site']} - {anime['trailer']['id']}"
                    file.write(f"Trailer: {trailer_url}\n")
                else:
                    file.write("Trailer: N/A\n")

                file.write("-등장인물-\n")
                for character in anime['characters']:
                    file.write(f"  이름: {character['name']}\n")
                    file.write(f"  특징: {character['description']}\n")
                    file.write(f"  성우: {', '.join(character['voice_actors'])}\n\n")
                file.write("\n")

                file.write("-제작진-\n")
                for staff_member in anime['staff']:
                    file.write(f" - {staff_member['role']}: {staff_member['name']} \n\n")
                file.write("\n")

                file.write(f"-제작사-: {', '.join(anime['studios'])}\n\n")
                file.write("\n" + "-" * 40 + "\n")
        print(f"Data from page {page_number} appended to 'anime_data.txt'")
    else:
        print(f"No data found on page {page_number}")
