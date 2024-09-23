# MySQL 접속 정보는 환경 변수로 전달받음
MYSQL_USER=${MYSQL_USER}
MYSQL_PASSWORD=${MYSQL_PASSWORD}
MYSQL_DATABASE=just

# MySQL 명령어 실행
mysql  -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <<EOF

INSERT INTO Refresh_token (email, refresh_token)
SELECT email, refresh_token
FROM Member;
EOF
