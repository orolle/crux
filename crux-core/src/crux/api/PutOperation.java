package crux.api;

import java.util.Map;
import java.util.HashMap;
import clojure.lang.PersistentVector;
import clojure.lang.Keyword;
import java.util.Date;
import java.util.UUID;
import java.net.URI;
import java.net.URL;

public class PutOperation implements Operation {
    private PersistentVector operation;
    private Map<Object, Object> query;
    private Date validTime;
    private boolean validTimeSet = false;

    public static class Builder implements OperationBuilder {
	private PersistentVector operation;
	private Map<Object, Object> query;
	private Date validTime;
	private boolean validTimeSet = false;

	public Builder() {
	    operation = PersistentVector.create();
	    operation = operation.cons(Keyword.intern("crux.tx/put"));
	    query = new HashMap<Object, Object>();
	}

	public OperationBuilder putId(String id) {
	    query.put(Keyword.intern("crux.db/id"), Keyword.intern(id));
	    return this;
	}

	public OperationBuilder putId(UUID id) {
	    query.put(Keyword.intern("crux.db/id"), id);
	    return this;
	}

	public OperationBuilder putId(URL id) {
	    query.put(Keyword.intern("crux.db/id"), id);
	    return this;
	}

	public OperationBuilder putId(URI id) {
	    query.put(Keyword.intern("crux.db/id"), id);
	    return this;
	}

	public OperationBuilder putValidTime(Date validtime) {
	    validTime = validtime;
	    validTimeSet = true;
	    return this;
	}

	public OperationBuilder put(String key, Object val) {
	    query.put(Keyword.intern(key), val);
	    return this;
	}

	public OperationBuilder put(Map<Object,Object> valueMap) {
	    query.putAll(valueMap);
	    return this;
	}

	public PutOperation build() {
	    PutOperation putOp = new PutOperation();
	    putOp.operation = operation;
	    putOp.query = query;
	    putOp.validTime = validTime;
	    putOp.validTimeSet = validTimeSet;
	    return putOp;
	}
    }
    public PersistentVector getOperation() {
	operation = operation.cons(query);
	if (validTimeSet)
	    operation = operation.cons(validTime);
	return operation;
    }
}
