package zookeeper.kotlin.zookeeper

import kotlin.Throws
import java.lang.IllegalArgumentException

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * This class is copied from Apache ZooKeeper.
 * The original class is not exported by ZooKeeper bundle and thus it can't be used in OSGi.
 * See issue: https://issues.apache.org/jira/browse/ZOOKEEPER-1627
 * A temporary workaround till the issue is resolved is to keep a copy of this class locally.
 */
object PathUtils {
    /** validate the provided znode path string
     * @param path znode path string
     * @param isSequential if the path is being created
     * with a sequential flag
     * @throws IllegalArgumentException if the path is invalid
     */
    @Throws(IllegalArgumentException::class)
    fun validatePath(path: String, isSequential: Boolean) {
        validatePath(if (isSequential) path + "1" else path)
    }

    /**
     * Validate the provided znode path string
     * @param path znode path string
     * @return The given path if it was valid, for fluent chaining
     * @throws IllegalArgumentException if the path is invalid
     */
    @Throws(IllegalArgumentException::class)
    fun validatePath(path: String?): String {
        requireNotNull(path) { "Path cannot be null" }
        require(path.length != 0) { "Path length must be > 0" }
        require(path[0] == '/') { "Path must start with / character" }
        if (path.length == 1) { // done checking - it's the root
            return path
        }
        require(path[path.length - 1] != '/') { "Path must not end with / character" }
        var reason: String? = null
        var lastc = '/'
        val chars = path.toCharArray()
        var c: Char
        var i = 1
        while (i < chars.size) {
            c = chars[i]
            if (c.code == 0) {
                reason = "null character not allowed @$i"
                break
            } else if (c == '/' && lastc == '/') {
                reason = "empty node name specified @$i"
                break
            } else if (c == '.' && lastc == '.') {
                if (chars[i - 2] == '/' &&
                    (i + 1 == chars.size
                            || chars[i + 1] == '/')
                ) {
                    reason = "relative paths not allowed @$i"
                    break
                }
            } else if (c == '.') {
                if (chars[i - 1] == '/' &&
                    (i + 1 == chars.size
                            || chars[i + 1] == '/')
                ) {
                    reason = "relative paths not allowed @$i"
                    break
                }
            } else if (c > '\u0000' && c < '\u001f' || c > '\u007f' && c < '\u009F' || c > '\ud800' && c < '\uf8ff' || c > '\ufff0' && c < '\uffff') {
                reason = "invalid charater @$i"
                break
            }
            lastc = chars[i]
            i++
        }
        require(reason == null) { "Invalid path string \"$path\" caused by $reason" }
        return path
    }
}