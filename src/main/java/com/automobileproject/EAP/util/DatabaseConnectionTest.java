package com.automobileproject.EAP.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔍 Testing database connection...");

        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ Database connected successfully!");
            System.out.println("📊 Database: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("🔗 URL: " + conn.getMetaData().getURL());
            System.out.println("👤 User: " + conn.getMetaData().getUserName());
            System.out.println("🚀 Driver: " + conn.getMetaData().getDriverName());
        } catch (Exception e) {
            System.out.println("❌ Database connection failed!");
            System.out.println("💥 Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}