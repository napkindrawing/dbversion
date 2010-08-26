package com.napkindrawing.dbversion.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;

public class DbVersionStatus extends DbVersionCommand {
    
    @Override
    public void execute() throws BuildException {
        
        super.execute();
        
        Connection conn = getConnection();
        Statement stmt = null;
        
        try {
            System.out.println("Querying Database: " + getUrl());
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM __database_revision");
            List<Map<String,String>> installedRevisions = new ArrayList<Map<String,String>>();
            
            String[] colNames = new String[] {
                "id",
                "profile",
                "version",
                "upgrade_script_name",
                "upgrade_script_template_checksum",
                "upgrade_script_compiled_checksum",
                "post_upgrade_schema_dump_checksum",
                "upgrade_date"
            };
            
            while(rs.next()) {
                Map<String,String> row = new HashMap<String, String>();
                for(String colName : colNames) {
                    row.put(colName, rs.getString(colName));
                }
                installedRevisions.add(row);
            }
            
            System.out.println(installedRevisions.size() + " Revisions Read");
            
        } catch(SQLException e) {
            if(e.getMessage().matches("^Table.*doesn't exist.*$")) {
                System.out.println("Database not initialized");
            } else {
                System.err.println("Error querying database: " + e.getMessage());
            }
        } finally  {
            if (stmt != null) {
                try {stmt.close();}catch (SQLException ignore) {}
            }
            if (conn != null) {
                try {conn.close();}catch (SQLException ignore) {}
            }
        }
        
    }
    
}
