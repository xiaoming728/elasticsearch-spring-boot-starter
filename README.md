# 搜索工具组件

ES搜索引擎SpringBoot工具组件

## 如何在 SpringBoot 环境中使用搜索工具
### 1、引入 Esearch 依赖
Esearch工具通过引用`elasticsearch-rest-high-level-client`工具包，封装了聚合、列表、搜索、统计等ES搜索引擎操作
```xml
<dependency>
    <groupId>com.echola</groupId>
    <artifactId>elasticsearch-spring-boot-starter</artifactId>
    <version>7.6.2-SNAPSHOT</version>
</dependency>
```

### 2、配置 Esearch 工具
在应用`application.yml`配置文件中加入以下配置
```yaml
# elasticsearch
spring.elasticsearch.rest:
  uris: http://192.168.206.180:9200,http://192.168.206.180:9200
  username:
  password:
  connection-timeout: 3000
  read-timeout: 3000
  connect:
    max-conn-total: 300
    max-conn-per-route: 300
    connection-request-timeout: 3000
    search-request-timeout: 3000
echola.elasticsearch.indexes:
  xxx-index: xxxxxxx
```

### 3、使用 Esearch 工具
```java
/**
 * 获取Esearch模板
 */
@Autowired
EsearchTemplate esearchTemplate;

@Value("${echola.elasticsearch.indexes.xxx-index}")
private String xxxIndex;

/**
 * 查询
 * 查询得到结果后会使用hits.tatal.value这个值作为匹配的总数
 */
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
PageHelper.startPage(1, 10);
List<JSONObject> voList = esearchTemplate.search(searchSourceBuilder, JSONObject.class, xxxIndex);
log.info(voList);

/**
 * 列表
 * 先查询count数，作为匹配的总数，再查询数据得到结果
 */
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
PageHelper.startPage(1, 10);
List<JSONObject> voList = esearchTemplate.select(searchSourceBuilder, JSONObject.class, xxxIndex);
log.info(voList);

/**
 * 搜索
 */
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
SearchResponse resulRes = esearchTemplate.search(searchSourceBuilder, xxxIndex);
log.info(resulRes);

/**
 * 聚合
 */
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
Aggregations result = esearchTemplate.aggregate(searchSourceBuilder, xxxIndex);
ParsedCardinality cardinality = result.get("taskCount");
log.info(cardinality.getValue());

/**
 * 计数
 */
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
CountResponse countRes = esearchTemplate.count(searchSourceBuilder, xxxIndex);
log.info(countRes.getCount());
```