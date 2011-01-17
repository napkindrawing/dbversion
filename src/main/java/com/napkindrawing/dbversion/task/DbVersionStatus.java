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

import com.napkindrawing.dbversion.Version;



public class DbVersionStatus extends DbVersionCommand {
    
    @Override
    public void execute() {
        
        super.execute();
        
        for(String profileName : getInstalledProfileNames()) {
            
            Version maxInstalled = getMaxInstalledVersion(profileName);
            Version max = getMaxVersion(profileName);

            String msg = maxInstalled.equals(max)
                       ? ""
                       : "   <=== OUT OF DATE";
            
            System.out.printf(
                "Installed/Available: %s/%s  Profile: %s%s\n",
                maxInstalled,
                max,
                profileName,
                msg
            );
        }
        
    }
}
