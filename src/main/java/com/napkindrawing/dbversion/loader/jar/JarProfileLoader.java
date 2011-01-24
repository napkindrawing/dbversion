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
package com.napkindrawing.dbversion.loader.jar;

import com.napkindrawing.dbversion.Profile;
import com.napkindrawing.dbversion.loader.ProfileLoader;
import com.napkindrawing.dbversion.loader.RevisionsLoader;

public class JarProfileLoader extends JarLoader implements ProfileLoader {
    
    RevisionsLoader revisionsLoader;
    
    /**
     * Null implementation, actual implementation is in {@link JarProfilesLoader}
     */
    @Override
    public Profile loadProfile(String profileName) {     
        return null;
    }

    @Override
    public void setRevisionsLoader(RevisionsLoader revisionsLoader) {
        this.revisionsLoader = revisionsLoader;
    }
    
}
