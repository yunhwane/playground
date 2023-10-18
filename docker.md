## docker 명령어 모음
- linux docker 로그 확인
```bash
docker logs --tail 20 -f [도커 컨테이너 id]
```
- docker image 확인
```bash
docker image
```
- docker 컨테이너 확인
```bash
docker ps
```
- docker 정지된 컨테이너 확인
```bash
docker ps -a
```
- docker 컨테이너 삭제
```bash
docker rm [container pid]
docker rm [container pid1], [container pid2]
```
- docker 컨테이너 모두 삭제
```bash
dokcer rm 'docker ps -a -q'
```
- docker 이미지 삭제
```bash
docker rmi [imagepid]
```
- docker 컨테이너 삭제전 이미지 삭제
```bash
docker rmi -f [imagepid]
```


