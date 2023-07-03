{i = 1}

query:
    create |  drop | select

create:
    CREATE TABLE {print(string.format("table%d", i)); i = i+1}  (a int)

drop:
    DROP TABLE {print(string.format("table%d", i - 1))}

select:
    SELECT * FROM (select) as tp  WHERE _field_int > _int
    | SELECT * FROM _table WHERE _field_int < _int
    | SELECT * FROM _table WHERE _field_char = _english
    | SELECT _field,COL_DOUBLE_KEY_SIGNED FROM _table
    | SELECT COL_FLOAT_KEY_SIGNED,COL_BIGINT_UNDEF_UNSIGNED FROM _table
    | SELECT  _field_int FROM _table WHERE COL_DOUBLE_KEY_SIGNED < 503.2
    | SELECT  _field_int FROM _table WHERE _field_int > 3554
    | SELECT  COL_BIGINT_KEY_UNSIGNED FROM _table WHERE _field_char = _english LIMIT 20
    | SELECT  _field_int_list FROM _table WHERE _field_int > 342 LIMIT 20
    | SELECT  _field_int_list , _field_int FROM _table ORDER BY COL_BIGINT_KEY_UNSIGNED LIMIT 20
    | SELECT  COL_BIGINT_KEY_UNSIGNED , _field_int FROM _table ORDER BY _field_int LIMIT 5 OFFSET 5
    | SELECT  A.COL_FLOAT_UNDEF_UNSIGNED B.COL_FLOAT_UNDEF_UNSIGNED FROM  _table A JOIN _table B LIMIT 20