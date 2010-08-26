package com.napkindrawing.dbversion.task;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;

public class DbVersionUpgrade extends DbVersionCommand {
    
    public DbVersionUpgrade() {
        super();
    }
    
    @Override
    public void execute() throws BuildException {
        super.execute();
        
        System.out.println("Upgrading database to latest version: " + getUrl());
    }
    
}
