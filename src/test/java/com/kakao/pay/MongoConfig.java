package com.kakao.pay;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import java.util.Arrays;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

//    @Value("${spring.data.mongodb.username}")
//    private String userName;
//
//    @Value("${spring.data.mongodb.password}")
//    private String password;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList());
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }

    public void init() {
        // mongoDB ram 사용량을 200mb 늘림.
        // db.adminCommand({setParameter: 1, internalQueryExecMaxBlockingSortBytes: 2e+8})
        DB db = mongoClient().getDB("admin");
        DBObject cmd = new BasicDBObject();
        cmd.put("setParameter", 1);
        cmd.put("internalQueryExecMaxBlockingSortBytes", 2e+8);
        CommandResult result = db.command(cmd);
    }
}
