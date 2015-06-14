/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tomitribe.hodaor;

import org.tomitribe.hodaor.impl.PersistenceHandler;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Singleton
@Lock(LockType.READ)
public abstract class BookCrud implements InvocationHandler {

    @PersistenceContext
    private EntityManager em;

    @Persist
    public abstract Book create(final Book book);

    @Persist
    public abstract void createVoid(final Book book);

    @Merge
    public abstract Book update(final Book book);

    @NamedQuery(Book.FIND_BY_TITLE)
    @Optional
    public abstract List<Book> findBooksByTitle(
        @QueryParam("title") final String title,
        @Offset final Integer offset,
        @MaxResults final Integer max);

    @NamedQuery(Book.FIND_ALL)
    @Optional
    public abstract List<Book> findAll(@Offset final Integer offset, @MaxResults final Integer max);

    @NamedQuery(update = true, value = Book.UPDATE_BOOKS_SET_YEAR)
    public abstract void setYearOnAllBooks(@QueryParam("year") final Long year);

    @NamedQuery(update = true, value = Book.UPDATE_BOOKS_SET_YEAR)
    public abstract String badUpdate(@QueryParam("year") final Long year);


    @NamedQuery(update = true, value = Book.DELETE_ALL)
    public abstract int deleteAll();

    @Find
    public abstract Book find(Long id);

    @NamedQuery(Book.FIND_BY_ID)
    public abstract Book findById(@QueryParam("id") Long id);

    @NamedQuery(Book.FIND_BY_ID)
    @Optional
    public abstract Book optionalFindById(@QueryParam("id") Long id);

    public abstract List<Book> dummy();

    public void deleteAllAndAdd(Book... books) {
        this.deleteAll();
        for (final Book book : books) {
            this.create(book);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return PersistenceHandler.invoke(this.em, method, args);
    }
}
