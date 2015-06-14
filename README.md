# Hodaor

**H**elper library for **DAO**s

*... other letters added for fun.  HODOR!*

Requires Apache TomEE 1.5.x or newer.

## Description

Hodaor takes advantage of Apache TomEE's abstract-bean concept.  The DAO is declared abstract and boilerplate methods can be simply annotated and handled by the framework.  Unlike purely interface-based approaches, this still allows you to use plain Java code for persistence logic that falls outside what the framework handles.

## Usage

To use, simply declare your EJB as abstract and implement `java.lang.reflect.InvocationHandler` as follows.

````
import org.tomitribe.hodaor.impl.PersistenceHandler;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Stateless
public abstract class BookCrud implements InvocationHandler {

    @PersistenceContext
    private EntityManager em;

    //...

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return PersistenceHandler.invoke(this.em, method, args);
    }
}
````

From here you can leverage the following annotations to abstract out common `javax.persistence.EntityManager` boilerplate.

- `org.tomitribe.hodaor.Find`
- `org.tomitribe.hodaor.Merge`
- `org.tomitribe.hodaor.Persist`
- `org.tomitribe.hodaor.Remove`
- `org.tomitribe.hodaor.NamedQuery`

All of which map to their simple JPA `EntityManager` equivalent.

### @Persist for `EntityManager.persist`

Valid examples of `@Persist` include:

````
    @Persist
    public abstract Book create(final Book book);

    @Persist
    public abstract void anotherAwesomeMovie(final Movie movie);
````

`@Persist` methods are effectively backed by the following boilerplate code in `PersistenceHandler`:

````
    public static Object persist(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getReturnType();
        final Object entity = args[0];

        if (entity == null) {
            throw new ValidationException(entityClass.getSimpleName() + " object is null");
        }

        em.persist(entity);

        if (isVoid(method.getReturnType())) {

            return null;

        } else {

            return entity;
        }
    }
````

### @Merge for `EntityManager.merge`

Valid examples of `@Merge` include:

````
    @Merge
    public abstract Book update(final Book book);

    @Merge
    public abstract Color update(final Color color);
````

`@Merge` methods are effectively backed by the following boilerplate code in `PersistenceHandler`:

````
    public static Object merge(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getReturnType();
        final Object entity = args[0];

        if (entity == null) {
            throw new ValidationException(entityClass.getSimpleName() + " object is null");
        }

        return em.merge(entity);
    }
````

### @Find for `EntityManager.find`

Valid examples of `@Find` include:

````
    @Find
    public abstract Book find(final Long bookId);

    @Find
    public abstract Author whoIsThis(final long authorId);

    @Find
    public abstract Cover giveMe(final int coverId);

    @Find
    public abstract Color lookFor(final ColorID customPrimaryKey);
````

`@Find` methods are effectively backed by the following boilerplate code in `PersistenceHandler`:

````
    public static Object findByPrimaryKey(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getReturnType();
        final Object primaryKey = args[0];

        if (primaryKey == null) {
            throw new ValidationException("Invalid id");
        }
        return em.find(entityClass, primaryKey);
    }
````


### @Remove for `EntityManager.remove`

Valid examples of `@Remove` include:

````
    @Remove
    public abstract void delete(final Book book);

    @Remove
    public abstract void rottenTomatoes(final Movie movie);
````

`@Remove` methods are effectively backed by the following boilerplate code in `PersistenceHandler`:

````
    public static Object remove(final EntityManager em, final Method method, final Object[] args) throws Throwable {
        final Class<?> entityClass = method.getReturnType();
        final Object entity = args[0];

        if (entity == null) {
            throw new ValidationException(entityClass.getSimpleName() + " object is null");
        }

        em.remove(em.merge(entity));

        return null;
    }
````



