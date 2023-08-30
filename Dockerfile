# Docker 镜像构建
# @author <a href="https://github.com/liyupi">外星桃子</a>
# @from <a href="https://yupi.icu">编程导航知识星球</a>
FROM maven:3.8.1-jdk-8-slim as builder

# 设置 Maven 镜像站点
RUN mkdir -p /root/.m2 && \
    echo '<settings> \
    <mirrors> \
    <mirror> \
    <id>aliyun</id> \
    <name>Aliyun Maven</name> \
    <url>https://maven.aliyun.com/repository/central</url> \
    <mirrorOf>central</mirrorOf> \
    </mirror> \
    </mirrors> \
    </settings>' > /root/.m2/settings.xml

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/taobi-backend-0.0.1-SNAPSHOT.jar"]