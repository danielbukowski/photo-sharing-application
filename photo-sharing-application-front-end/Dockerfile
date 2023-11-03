FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . /app
RUN npm run build

FROM nginx:alpine
COPY nginx/default.conf /etc/nginx/conf.d
COPY --from=build /app/dist/photo-sharing-application-front-end/ /usr/share/nginx/html
