# Repository Guidelines

## 项目结构与模块组织
- 根目录 `pom.xml` 作为 Maven Reactor，对齐所有模块的依赖与插件版本。
- `web-spring-boot-context/` 保存可复用的上下文组件；业务 Bean 放在 `src/main/java`，配置样例置于 `src/main/resources`。
- `web-spring-boot-autoconfigure/` 编写条件装配与元数据，务必维护 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`。
- `web-spring-boot-starter/` 打包对外发布的 Starter，并提供 `additional-spring-configuration-metadata.json` 等提示文件。
- 测试代码与模块同级，位于 `src/test/java`，共享夹具优先放在 context 模块。

## 构建、测试与开发命令
- `mvn -T 1C clean install` 并行构建全部模块；迭代阶段可附加 `-DskipTests=true` 加快速度。
- `mvn test` 运行 JUnit 5 套件；局部测试使用 `mvn -pl web-spring-boot-autoconfigure test` 等精确模块选择。
- `mvn license:format` 在 `validate` 阶段自动补齐 Apache 2.0 版权头；提交前确认无缺失文件。
- 发布前执行 `mvn -Prelease deploy`，保证 `main` 分支干净且 GPG 签名配置正确。

## 编码风格与命名约定
- 统一使用 Java 17 与四空格缩进，控制每行不超过 120 字符。
- 按 Spring Boot 惯例命名：配置类以 `Configuration` 收尾，自动装配以 `AutoConfiguration` 结束，条件工具以 `Condition` 结尾。
- 默认值放在 `src/main/resources/application.properties`，并通过 `@ConfigurationProperties` 暴露，类名以 `Properties` 结尾。
- 所有源码保持 Apache 2.0 版权头，禁止绕过 `license-maven-plugin`。

## 测试指南
- 使用 JUnit 5 与 AssertJ；测试类命名 `<功能名>Tests`，包结构镜像生产代码。
- 仅在必须启动完整上下文时使用 `@SpringBootTest`，其余场景采用 Web 或配置切片缩短执行时间。
- 为每个自动装配编写正反向测试，验证条件满足与失败时的 Bean 状态。
- 公共 Starter API 变更需补充集成测试，并记录示例配置于 `src/test/resources`。

## 提交与合并请求规范
- 采用 Conventional Commits（如 `feat:`, `fix:`, `chore:`），可选添加模块作用域。
- 每次提交聚焦单一功能，同时更新相关文档、配置或示例。
- 合并请求需描述改动背景、链接关联 Issue，并列出验证步骤（例如 `mvn clean install`）。
- 如改动影响可观测行为，例如 Actuator 暴露内容或 Starter 元数据，请附日志或截图证明。

## 沟通偏好
- 默认使用中文回复，除非对方明确要求使用其他语言。
