FROM docker.elastic.co/elasticsearch/elasticsearch:8.7.1

# 노리 형태소 분석기 설치
RUN elasticsearch-plugin install analysis-nori