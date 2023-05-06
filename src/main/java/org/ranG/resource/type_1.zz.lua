tables = {
    rows = { 30, 90 , 120},
    -- SHOW CHARACTER SET;
    charsets = {'utf8', 'latin1', 'binary'},
    -- partition number
    partitions = {4, 6, 'undef'},
}

fields = {
    types = {'bigint', 'float', 'double', 'decimal(40, 20)',
        'char(20)', 'varchar(20)', 'enum'},
    sign = {'signed', 'unsigned'}
}

data = {
    numbers = {'null', 'tinyint', 'smallint',
        '12.991', '1.009', '-9.183','4.544',
        'decimal',
    },
    strings = {'null', 'letter', 'english'},
    bigint = {100,10,3},
}