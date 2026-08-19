# Production Dockerfile for TubeMaster AI Account Deletion Web Service
FROM node:20-alpine

WORKDIR /app

# Copy web service files
COPY package.json ./
COPY server.js ./
COPY index.html ./
COPY privacy.html ./
COPY style.css ./
COPY script.js ./
COPY account-deletion-web ./account-deletion-web

ENV NODE_ENV=production
ENV PORT=8080
ENV SUPPORT_EMAIL=hloob07@gmail.com
ENV PRIVACY_POLICY_URL=/privacy

EXPOSE 8080

CMD ["node", "server.js"]
