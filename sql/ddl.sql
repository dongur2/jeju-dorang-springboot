-- DB 생성 후 권한 삽입 필요 (USER, ADMIN)
INSERT INTO role(name) VALUES('USER');
INSERT INTO role(name) VALUES('ADMIN');

-- Trip
INSERT INTO trip(address, category, image, introduction, name, place_id, tags, tel, thumbnail)
VALUES ('제주특별자치도 제주시 사장길 14', '음식점',
        'https://api.cdn.visitjeju.net/photomng/imgpath/201804/30/893e9b44-b2a1-4aa3-8404-870197d6d9fb.jpg',
        '제주도 향토음식을 다양하게 즐길 수 있는 곳', '황금어장(제주시)', 'CNTS_000000000001507',
        '향토음식,옥돔미역국,갈치호박국,성게국,방어회,음식,갈치조림,제주갈치조림,제주갈치구이,갈치구이,고등어구이,고등어조림,방어,물회,한치물회,자리물회,성게물회,전복물회,전복성게물회,소라물회,성게미역국,성게전복미역국,갈치국,뚝배기,전복뚝배기,전복죽,몸국,회덮밥,비빔밥,우럭조림,옥돔구이,전복구이,회,모둠회,황돔회,광어회,고등어회,도다리회,전복회,소라회,한치회,공용주차장,현금결제,카드결제,화장실,음료',
        '064-748-8989',
        'https://api.cdn.visitjeju.net/photomng/thumbnailpath/201804/30/2e748ed8-1509-4a85-9ac6-b47aedfc97ac.jpg');


