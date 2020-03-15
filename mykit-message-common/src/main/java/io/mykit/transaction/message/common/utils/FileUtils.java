/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mykit.transaction.message.common.utils;

import io.mykit.transaction.message.common.exception.MykitRuntimeException;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author binghe
 * @version 1.0.0
 * @description FileUtils
 */
public class FileUtils {

    /**
     * writeFile.
     *
     * @param fullFileName file path
     * @param contents     contents
     */
    public static void writeFile(final String fullFileName, final byte[] contents) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fullFileName, "rw");
            try (FileChannel channel = raf.getChannel()) {
                ByteBuffer buffer = ByteBuffer.allocate(contents.length);
                buffer.put(contents);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MykitRuntimeException(e);
        } finally { // add by eddy
            if (null != raf) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MykitRuntimeException(e);
                }
            }
        }
    }
}
