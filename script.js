import http from 'k6/http';
import { check } from 'k6';

function getRandomUserId(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

export let options = {
    scenarios: {
        constant_request_rate: {
            executor: 'ramping-arrival-rate',
            startRate: 167, // 초당 약 167개의 요청으로 시작
            timeUnit: '1s',
            preAllocatedVUs: 50, // 초기 할당 VUs 수
            maxVUs: 100, // 최대 VUs 수
            stages: [
                { duration: '1m', target: 167 }, // 1분 동안 초당 약 167개의 요청을 유지
            ],
        },
    },
};

let requestCount = 0; // 요청 횟수를 추적

export default function () {
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // 첫 번째 API (GET) 호출
    let response1 = http.get('http://3.38.113.85:9000/api/get/post?request_page=5', params);
    check(response1, {
        'First API status is 200': (r) => r.status === 200,
    });

    // 요청 횟수 증가
    requestCount++;

    // 3번째 GET 호출마다 POST 호출
    if (requestCount % 3 === 0) {
        const randomMemberId = getRandomUserId(1, 10000); // 랜덤 member_id 생성
        const payload = JSON.stringify({
            content: "Test content", // 게시글 내용
            hash_tag: ["example", "test"], // 해시태그 배열
            post_picture: 1, // 게시글 사진 ID
            secret: true, // 비밀 게시글 여부
        });

        let response2 = http.post(
            `http://3.38.113.85:9000/api/test/post/post?member_id=${randomMemberId}`,
            payload,
            params
        );

        check(response2, {
            'Second API status is 200': (r) => r.status === 200, // 기대하는 상태 코드
        });
    }
}
