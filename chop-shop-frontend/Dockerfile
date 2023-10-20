#docker build -t chopshop-front:1.0 .
FROM node:21-alpine as deps

WORKDIR /build

COPY package.json .
COPY yarn.lock .

RUN yarn install --verbose --production --non-interactive

FROM busybox:1.36

WORKDIR /home

COPY --from=deps build/node_modules/bootstrap/dist/css/bootstrap.min.css bootstrap/css/
COPY --from=deps build/node_modules/bootstrap-icons/font/bootstrap-icons.min.css bootstrap-icons/font/
COPY --from=deps build/node_modules/bootstrap-icons/font/fonts/bootstrap-icons.woff bootstrap-icons/font/fonts/
COPY --from=deps build/node_modules/bootstrap-icons/font/fonts/bootstrap-icons.woff2 bootstrap-icons/font/fonts/

COPY image image
COPY script script
COPY style style
COPY home.html index.html
COPY checkout.html .

EXPOSE 3000

CMD ["/bin/sh", "-c", "echo 'export function backend() { return \"'$API_URL'\" }' > /tmp/env.js > /home/script/env.js; busybox httpd -f -v -p3000"]
