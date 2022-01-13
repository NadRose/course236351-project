package zookeeper.kotlin.zookeeper

import kotlin.jvm.JvmOverloads
import zookeeper.kotlin.zookeeper.ZKPaths
import zookeeper.kotlin.zookeeper.ZKPaths.PathAndNode
import java.lang.StringBuilder

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
object ZKPaths {
    /**
     * Zookeeper's path separator character.
     */
    const val PATH_SEPARATOR = "/"
    private const val PATH_SEPARATOR_CHAR = '/'
    /**
     * Apply the namespace to the given path
     *
     * @param namespace    namespace (can be null)
     * @param path         path
     * @param isSequential if the path is being created with a sequential flag
     * @return adjusted path
     */
    /**
     * Apply the namespace to the given path
     *
     * @param namespace namespace (can be null)
     * @param path      path
     * @return adjusted path
     */
    @JvmOverloads
    fun fixForNamespace(namespace: String?, path: String?, isSequential: Boolean = false): String? {
        // Child path must be valid in and of itself.
        PathUtils.validatePath(path!!, isSequential)
        return if (namespace != null) {
            makePath(namespace, path)
        } else path
    }

    /**
     * Given a full path, return the node name. i.e. "/one/two/three" will return "three"
     *
     * @param path the path
     * @return the node
     */
    fun getNodeFromPath(path: String): String {
        PathUtils.validatePath(path)
        val i = path.lastIndexOf(PATH_SEPARATOR_CHAR)
        if (i < 0) {
            return path
        }
        return if (i + 1 >= path.length) {
            ""
        } else path.substring(i + 1)
    }

    /**
     * Given a full path, return the node name and its path. i.e. "/one/two/three" will return {"/one/two", "three"}
     *
     * @param path the path
     * @return the node
     */
    fun getPathAndNode(path: String): PathAndNode {
        PathUtils.validatePath(path)
        val i = path.lastIndexOf(PATH_SEPARATOR_CHAR)
        if (i < 0) {
            return PathAndNode(path, "")
        }
        if (i + 1 >= path.length) {
            return PathAndNode(PATH_SEPARATOR, "")
        }
        val node = path.substring(i + 1)
        val parentPath = if (i > 0) path.substring(0, i) else PATH_SEPARATOR
        return PathAndNode(parentPath, node)
    }

    // Hardcoded in {@link org.apache.zookeeper.server.PrepRequestProcessor}
    const val SEQUENTIAL_SUFFIX_DIGITS = 10

    /**
     * Extracts the ten-digit suffix from a sequential znode path. Does not currently perform validation on the
     * provided path; it will just return a string comprising the last ten characters.
     *
     * @param path the path of a sequential znodes
     * @return the sequential suffix
     */
    fun extractSequentialSuffix(path: String): String {
        val length = path.length
        return if (length > SEQUENTIAL_SUFFIX_DIGITS) path.substring(length - SEQUENTIAL_SUFFIX_DIGITS) else path
    }

    /**
     * Given a parent path and a child node, create a combined full path
     *
     * @param parent the parent
     * @param child  the child
     * @return full path
     */
    fun makePath(parent: String?, child: String?): String {
        // 2 is the maximum number of additional path separators inserted
        val maxPathLength = nullableStringLength(parent) + nullableStringLength(child) + 2
        // Avoid internal StringBuilder's buffer reallocation by specifying the max path length
        val path = StringBuilder(maxPathLength)
        joinPath(path, parent, child)
        return path.toString()
    }

    /**
     * Given a parent path and a list of children nodes, create a combined full path
     *
     * @param parent       the parent
     * @param firstChild   the first children in the path
     * @param restChildren the rest of the children in the path
     * @return full path
     */
    fun makePath(parent: String?, firstChild: String?, vararg restChildren: String?): String {
        // 2 is the maximum number of additional path separators inserted
        var maxPathLength = nullableStringLength(parent) + nullableStringLength(firstChild) + 2
        if (restChildren != null) {
            for (child in restChildren) {
                // 1 is for possible additional separator
                maxPathLength += nullableStringLength(child) + 1
            }
        }
        // Avoid internal StringBuilder's buffer reallocation by specifying the max path length
        val path = StringBuilder(maxPathLength)
        joinPath(path, parent, firstChild)
        return if (restChildren == null) {
            path.toString()
        } else {
            for (child in restChildren) {
                joinPath(path, "", child)
            }
            path.toString()
        }
    }

    private fun nullableStringLength(s: String?): Int {
        return s?.length ?: 0
    }

    /**
     * Given a parent and a child node, join them in the given [path][StringBuilder]
     *
     * @param path   the [StringBuilder] used to make the path
     * @param parent the parent
     * @param child  the child
     */
    private fun joinPath(path: StringBuilder, parent: String?, child: String?) {
        // Add parent piece, with no trailing slash.
        if (parent != null && parent.length > 0) {
            if (parent[0] != PATH_SEPARATOR_CHAR) {
                path.append(PATH_SEPARATOR_CHAR)
            }
            if (parent[parent.length - 1] == PATH_SEPARATOR_CHAR) {
                path.append(parent, 0, parent.length - 1)
            } else {
                path.append(parent)
            }
        }
        if (child == null || child.length == 0 ||
            child.length == 1 && child[0] == PATH_SEPARATOR_CHAR
        ) {
            // Special case, empty parent and child
            if (path.length == 0) {
                path.append(PATH_SEPARATOR_CHAR)
            }
            return
        }

        // Now add the separator between parent and child.
        path.append(PATH_SEPARATOR_CHAR)
        val childAppendBeginIndex: Int
        childAppendBeginIndex = if (child[0] == PATH_SEPARATOR_CHAR) {
            1
        } else {
            0
        }
        val childAppendEndIndex: Int
        childAppendEndIndex = if (child[child.length - 1] == PATH_SEPARATOR_CHAR) {
            child.length - 1
        } else {
            child.length
        }

        // Finally, add the child.
        path.append(child, childAppendBeginIndex, childAppendEndIndex)
    }

    class PathAndNode(val path: String, val node: String) {

        override fun hashCode(): Int {
            val prime = 31
            var result = 1
            result = prime * result + node.hashCode()
            result = prime * result + path.hashCode()
            return result
        }

        override fun equals(obj: Any?): Boolean {
            if (this === obj) {
                return true
            }
            if (obj == null) {
                return false
            }
            if (javaClass != obj.javaClass) {
                return false
            }
            val other = obj as PathAndNode
            if (node != other.node) {
                return false
            }
            return if (path != other.path) {
                false
            } else true
        }

        override fun toString(): String {
            return "PathAndNode [path=$path, node=$node]"
        }
    }
}