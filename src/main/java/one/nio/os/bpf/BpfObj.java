/*
 * Copyright 2021 Odnoklassniki Ltd, Mail.Ru Group
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

package one.nio.os.bpf;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class BpfObj extends Handle {
    public static final int MAX_NAME_LEN = 15;

    public final int id;
    public final String name;

    public BpfObj(int id, String name, int fd) {
        super(fd);
        this.id = id;
        this.name = name;
    }

    public void pin(String path) throws IOException {
        Bpf.objectPin(fd(), path);
    }

    static class IdsIterator implements Iterator<Integer> {
        final int type;
        int next;
        boolean nextChecked;

        IdsIterator(int type) {
            this.type = type;
        }

        @Override
        public boolean hasNext() {
            if (!nextChecked) {
                next = Bpf.objGetNextId(type, next);
                nextChecked = true;
            }
            return next > 0;
        }

        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            nextChecked = false;
            return next;
        }
    }
}
