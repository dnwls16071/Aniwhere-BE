import mysql.connector

def create_tables():
    try:
        # MySQL 연결 설정
        connection = mysql.connector.connect(
            host="localhost",  # MySQL 호스트
            user="root",  # MySQL 사용자 이름
            password="1234",  # MySQL 비밀번호
            database="anime_db"  # 사용할 데이터베이스
        )
        cursor = connection.cursor()

        create_anime_table = """
        CREATE TABLE IF NOT EXISTS Anime (
            anime_id INT PRIMARY KEY AUTO_INCREMENT,
            title VARCHAR(255) NULL,
            director VARCHAR(255) NULL,
            character_design VARCHAR(255) NULL,          
            music_director VARCHAR(255) NULL, 
            animation_director VARCHAR(255) NULL, 
            script VARCHAR(255) NULL, 
            producer VARCHAR(255) NULL,
            studio VARCHAR(255) NULL,
            release_date DATE NULL, 
            end_date DATE NULL,
            episodes VARCHAR(255) NULL,                            
            running_time VARCHAR(255) NULL,                     
            status VARCHAR(50) NULL, 
            trailer VARCHAR(255) NULL, 
            description TEXT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            poster VARCHAR(255) NULL,
            airing_quarter INT NULL,
            is_adult BOOLEAN NULL,
            duration VARCHAR(255) NULL,
            weekday VARCHAR(255) NULL,
            anilist_id INT NUll
        );
        """

        create_categories_table = """
        CREATE TABLE IF NOT EXISTS Categories (
            category_id INT AUTO_INCREMENT PRIMARY KEY,
            category_name VARCHAR(255) NULL
        );
        """

        create_voice_actors_table = """
        CREATE TABLE IF NOT EXISTS VoiceActors (
            voice_actor_id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NULL
        );
        """

        create_casting_table = """
        CREATE TABLE IF NOT EXISTS Casting (
            casting_id INT AUTO_INCREMENT PRIMARY KEY,
            anime_id INT NULL,
            voice_actor_id INT NULL,
            character_name VARCHAR(255) NULL,
            character_description TEXT NULL,
            FOREIGN KEY (anime_id) REFERENCES anime(anime_id),
            FOREIGN KEY (voice_actor_id) REFERENCES voiceActors(voice_actor_id)
        );
        """

        create_anime_categories_table = """
        CREATE TABLE IF NOT EXISTS AnimeCategories (
            anime_id INT NULL,
            category_id INT NULL,
            FOREIGN KEY (anime_id) REFERENCES anime(anime_id),
            FOREIGN KEY (category_id) REFERENCES categories(category_id)
        );
        """

        cursor.execute(create_anime_table)
        cursor.execute(create_categories_table)
        cursor.execute(create_voice_actors_table)
        cursor.execute(create_casting_table)
        cursor.execute(create_anime_categories_table)

        # 변경사항 커밋
        connection.commit()

        print("Tables created successfully.")

    except mysql.connector.Error as err:
        print(f"Error: {err}")

    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()


# 함수 실행
create_tables()
