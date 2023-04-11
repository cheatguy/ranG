package org.ranG.grammar.SqlGenerator;

public interface SQLIterator {
    int visit(SQLVisitor visitor);  //return value < 0 is error
    // pathinfo
    PathInfo pathInfo();
}
