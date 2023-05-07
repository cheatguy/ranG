{i = 1}

query:
    select

select:
    SELECT * FROM (select) as tp  WHERE _field_int > _int
    | SELECT * FROM _table WHERE _field_int < _int
    | SELECT * FROM _table WHERE _field_char = _english
    | SELECT col_double_undef_signed FROM _table
    | SELECT col_float_key_signed FROM _table
