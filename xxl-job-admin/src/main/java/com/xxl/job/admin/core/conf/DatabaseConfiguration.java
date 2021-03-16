package com.xxl.job.admin.core.conf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


@Configuration
@EnableConfigurationProperties({MybatisPlusProperties.class, DataSourceProperties.class})
@MapperScan("com.xxl.job.admin.dao")
public class DatabaseConfiguration {
    private final static HashMap<DbType, String> DB_QUOTE = new HashMap<>(16);

    static {
        DB_QUOTE.put(DbType.MYSQL, "`%s`");
        DB_QUOTE.put(DbType.MARIADB, "`%s`");
        DB_QUOTE.put(DbType.ORACLE, null);
        DB_QUOTE.put(DbType.DB2, null);
        DB_QUOTE.put(DbType.H2, null);
        DB_QUOTE.put(DbType.HSQL, null);
        DB_QUOTE.put(DbType.SQLITE, "`%s`");
        DB_QUOTE.put(DbType.POSTGRE_SQL, "\"%s\"");
        DB_QUOTE.put(DbType.SQL_SERVER2005, null);
        DB_QUOTE.put(DbType.SQL_SERVER, null);
        DB_QUOTE.put(DbType.OTHER, null);
    }

    private final MybatisPlusProperties properties;
    private final ResourceLoader resourceLoader;
    private final DataSourceProperties dataSourceProperties;

    public DatabaseConfiguration(MybatisPlusProperties properties,
                                 ResourceLoader resourceLoader,
                                 DataSourceProperties dataSourceProperties) {
        System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
        this.properties = properties;
        this.resourceLoader = resourceLoader;
        this.dataSourceProperties = dataSourceProperties;
    }


    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(DataSource dataSource) {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            mybatisSqlSessionFactoryBean.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        // 插件
        mybatisSqlSessionFactoryBean.setPlugins(getInterceptors());

        GlobalConfig globalConfig = this.properties.getGlobalConfig();
        globalConfig.setBanner(false);
        // 逻辑删除配置
        globalConfig.getDbConfig().setLogicDeleteValue("1");
        globalConfig.getDbConfig().setLogicNotDeleteValue("0");
        // 自动格式化(历史数据库保留字)
        DbType dbType = JdbcUtils.getDbType(dataSourceProperties.getUrl());
        globalConfig.getDbConfig().setColumnFormat(DB_QUOTE.get(dbType));
        // 注入SqlRunner
        globalConfig.setEnableSqlRunner(true);
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);
        if ("oracle".equals(dbType.getDb())){
            globalConfig.getDbConfig().setKeyGenerator(new OracleKeyGenerator());
            globalConfig.getDbConfig().setIdType(IdType.INPUT);
        }else{
            globalConfig.getDbConfig().setIdType(IdType.AUTO);
        }

        MybatisConfiguration configuration = this.properties.getConfiguration();
        if (configuration == null) {
            configuration = new MybatisConfiguration();
        }
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        //开启驼峰
        configuration.setMapUnderscoreToCamelCase(true);
        //配置JdbcTypeForNull, oracle数据库必须配置
        configuration.setJdbcTypeForNull(JdbcType.VARCHAR);

        mybatisSqlSessionFactoryBean.setConfiguration(configuration);
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            mybatisSqlSessionFactoryBean.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            mybatisSqlSessionFactoryBean.setMapperLocations(this.properties.resolveMapperLocations());
        }
        if (!StringUtils.isEmpty(this.properties.getTypeHandlersPackage())) {
            mybatisSqlSessionFactoryBean.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        // 提供databaseId 需要使用特定的数据库函数时，可以根据_databaseId进行判断
        mybatisSqlSessionFactoryBean.setDatabaseIdProvider(getDatabaseIdProvider());
        if (!StringUtils.isEmpty(this.properties.getTypeEnumsPackage())) {
            mybatisSqlSessionFactoryBean.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
        }
        return mybatisSqlSessionFactoryBean;
    }

    /**
     * 插件  登录信息注入 -> 填充 -> 分页 -> 乐观锁 -> 多语言 -> 数据权限
     *
     * @return Interceptor[]
     */
    private Interceptor[] getInterceptors() {
        List<Interceptor> interceptors = new ArrayList<Interceptor>();
        // 考虑到公用字段填充与部分方法冲突，现使用拦截器创建时公用字段enabled、deleted、versionNumber
        PaginationInterceptor pagination = new PaginationInterceptor();
        interceptors.add(pagination);
        return interceptors.toArray(new Interceptor[]{});
    }


    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("DB2", "db2");
        properties.setProperty("Derby", "derby");
        properties.setProperty("H2", "h2");
        properties.setProperty("HSQL", "hsql");
        properties.setProperty("Informix", "informix");
        properties.setProperty("MS-SQL", "ms-sql");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("Sybase", "sybase");
        properties.setProperty("Hana", "hana");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

}
