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