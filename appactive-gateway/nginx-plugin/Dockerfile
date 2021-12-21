FROM docker.io/openresty/openresty:1.19.9.1-3-centos

WORKDIR /etc/nginx
EXPOSE 80
EXPOSE 8090

ARG UNITFLAG=default

ENV TZ=Asia/Shanghai \
   UNITFLAG=$UNITFLAG \
   LANG="en_US.UTF-8"

RUN mkdir -p /etc/nginx/logs && mkdir -p /etc/nginx/store && chmod 777 /etc/nginx/store

ADD ./etc/nginx/conf /etc/nginx/conf

CMD ["nginx", "-p", "/etc/nginx", "-c", "/etc/nginx/conf/nginx.conf", "-g", "daemon off;"]
# nginx -p /etc/nginx -c /etc/nginx/conf/nginx.conf -g  "daemon off;"
