package com.napkindrawing.dbversion.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;

public class DbVersionInit extends DbVersionCommand {
    
    private Boolean forceInit = false;
    
    public DbVersionInit() {
        super();
        checkInitTables = false;
    }
    
    @Override
    public void execute() throws BuildException {
        super.execute();
        
        if(canQueryRevisionTable()) {
            if(forceInit) {
                System.out.println("Removing existing revision tables");
            } else {
                throw new BuildException("Database at " + getUrl() + " already initialized, to reinitialize set forceInit=true");
            }
        }
        
        Connection conn = getConnection();
        Statement createStmt = null;
        Statement dropStmt = null;
        
        String createSql = loadResourceFile("com/napkindrawing/dbversion/createRevisionTable.sql");
        
        try {
            System.out.println("Initializing Database: " + getUrl());
            
            dropStmt = conn.createStatement();
            dropStmt.executeUpdate("DROP TABLE IF EXISTS `__database_revision`");
            
            createStmt = conn.createStatement();
            createStmt.executeUpdate(createSql);            
            
        } catch(SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        } finally  {
            if (dropStmt != null) {
                try {dropStmt.close();}catch (SQLException ignore) {}
            }
            if (createStmt != null) {
                try {createStmt.close();}catch (SQLException ignore) {}
            }
            if (conn != null) {
                try {conn.close();}catch (SQLException ignore) {}
            }
        }
        System.out.println(" ... done!");
    }

    public void setForceInit(Boolean forceInit) {
        this.forceInit = forceInit;
    }
    
}
