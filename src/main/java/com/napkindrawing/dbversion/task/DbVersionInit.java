/*
 * Copyright 2010 NapkinDrawing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.napkindrawing.dbversion.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
        Statement logStmt = null;
        
        String createSql = loadResourceFile("com/napkindrawing/dbversion/createRevisionTable.sql");
        
        try {
            System.out.println("Initializing Database: " + getUrl());

            logStmt = conn.createStatement();
            logStmt.execute("SELECT '"+Thread.currentThread()+"' AS thread");
            
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
            if (logStmt != null) {
                try {logStmt.close();}catch (SQLException ignore) {}
            }
            if (createStmt != null) {
                try {createStmt.close();}catch (SQLException ignore) {}
            }
            if (conn != null) {
                try {conn.close();}catch (SQLException ignore) {}
            }
        }
    }

    public void setForceInit(Boolean forceInit) {
        this.forceInit = forceInit;
    }
    
}
