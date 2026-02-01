-- 계정 생성 후 데이터베이스 활용

-- 새로운 discodeit_user 계정 만들기
CREATE USER discodeit_user
    PASSWORD 'discodeit1234' -- 비밀번호 설정
    CREATEDB;
-- 데이터베이스 생성 권한 부여

-- 데이터베이스 생성
CREATE DATABASE discodeit
    WITH
    OWNER = discodeit_user
    ENCODING = 'UTF8';

-- 권한 부여
-- public 스키마에 대한 권한을 부여합니다.
GRANT ALL PRIVILEGES ON DATABASE discodeit TO discodeit_user;
GRANT ALL ON SCHEMA public TO discodeit_user;
