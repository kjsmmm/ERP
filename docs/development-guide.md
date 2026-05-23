# 开发环境搭建指南

## 环境要求

### 必需软件

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Java 开发工具包 |
| Maven | 3.8+ | Java 构建工具 |
| Node.js | 18+ | JavaScript 运行时 |
| npm | 9+ | Node.js 包管理器 |
| Docker | 24+ | 容器化平台 |
| Docker Compose | 2.20+ | 容器编排工具 |
| Git | 2.40+ | 版本控制工具 |

### 推荐 IDE

- **后端开发**: IntelliJ IDEA (推荐) 或 Eclipse
- **前端开发**: Visual Studio Code (推荐) 或 WebStorm

## 环境搭建步骤

### 1. 安装 JDK 17

#### Windows
1. 下载 JDK 17: https://adoptium.net/
2. 安装并配置环境变量:
   ```
   JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.x.x
   PATH=%JAVA_HOME%\bin;%PATH%
   ```
3. 验证安装: `java -version`

#### macOS
```bash
# 使用 Homebrew
brew install openjdk@17

# 配置环境变量
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# CentOS/RHEL
sudo yum install java-17-openjdk-devel
```

### 2. 安装 Maven

#### Windows
1. 下载 Maven: https://maven.apache.org/download.cgi
2. 解压到目录，如 `C:\Program Files\Apache\maven`
3. 配置环境变量:
   ```
   MAVEN_HOME=C:\Program Files\Apache\maven
   PATH=%MAVEN_HOME%\bin;%PATH%
   ```
4. 验证安装: `mvn -version`

#### macOS
```bash
brew install maven
```

#### Linux
```bash
sudo apt install maven  # Ubuntu/Debian
sudo yum install maven  # CentOS/RHEL
```

### 3. 安装 Node.js

#### Windows
1. 下载 Node.js: https://nodejs.org/
2. 安装 LTS 版本
3. 验证安装: `node -v` 和 `npm -v`

#### macOS
```bash
brew install node@18
```

#### Linux
```bash
# 使用 NodeSource
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs
```

### 4. 安装 Docker

#### Windows
1. 下载 Docker Desktop: https://www.docker.com/products/docker-desktop/
2. 安装并启动
3. 验证安装: `docker --version` 和 `docker-compose --version`

#### macOS
1. 下载 Docker Desktop for Mac
2. 安装并启动

#### Linux
```bash
# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 将用户添加到 docker 组
sudo usermod -aG docker $USER
```

## 项目搭建

### 1. 克隆项目

```bash
git clone <repository-url>
cd ERP-OpenSpec
```

### 2. 启动数据库

```bash
cd docker

# 复制环境变量配置
cp .env.example .env

# 编辑配置文件（修改密码等）
vim .env

# 启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

服务启动后：
- MySQL: localhost:3306
- Redis: localhost:6379
- Adminer: http://localhost:8080

### 3. 启动后端

```bash
cd erp-backend

# 编译项目
mvn clean package -DskipTests

# 运行应用
java -jar erp-boot/target/erp-boot-1.0.0.jar

# 或者使用 Maven 运行
mvn spring-boot:run -pl erp-boot
```

后端启动后：
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html

### 4. 启动前端

```bash
cd erp-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后：
- 访问: http://localhost:3000

### 5. 登录系统

- 用户名: `admin`
- 密码: `admin123456`

## 开发工具配置

### IntelliJ IDEA

1. 导入项目: File → Open → 选择 `erp-backend` 目录
2. 配置 JDK: File → Project Structure → SDKs → 添加 JDK 17
3. 安装 Lombok 插件: File → Settings → Plugins → 搜索 Lombok
4. 启用注解处理: File → Settings → Build → Compiler → Annotation Processors → 勾选 Enable annotation processing

### VS Code

1. 安装扩展:
   - Vue - Official
   - ESLint
   - Prettier
   - TypeScript Vue Plugin (Volar)

2. 配置设置:
   ```json
   {
     "editor.formatOnSave": true,
     "editor.defaultFormatter": "esbenp.prettier-vscode",
     "typescript.tsdk": "node_modules/typescript/lib"
   }
   ```

## 常见问题

### 1. Maven 依赖下载慢

配置阿里云镜像:

```xml
<!-- settings.xml -->
<mirrors>
    <mirror>
        <id>aliyunmaven</id>
        <mirrorOf>*</mirrorOf>
        <name>阿里云公共仓库</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

### 2. Docker 启动失败

```bash
# 检查 Docker 是否运行
docker info

# 查看容器日志
docker-compose logs mysql
docker-compose logs redis

# 重新创建容器
docker-compose down
docker-compose up -d
```

### 3. 端口被占用

```bash
# Windows
netstat -ano | findstr :3306
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :3306
kill -9 <PID>
```

### 4. 数据库连接失败

1. 检查 MySQL 是否启动: `docker-compose ps`
2. 检查端口是否正确: 3306
3. 检查用户名密码是否正确
4. 检查数据库是否创建: `erp`

## 下一步

- 阅读 [项目文档](../项目文档.md) 了解系统设计
- 查看 [API 文档](http://localhost:8080/api/swagger-ui.html) 了解接口
- 开始开发第一个业务模块
