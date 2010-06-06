#!/usr/bin/ruby

def _quote(val)
return "\"" , val,"\""
end

def _drop_if_exists_and_create_table(name,definition)
puts "call drop_if_exists( '#{name}' );"
puts "CREATE TABLE #{name}"
puts "(\n",definition ,");"
end

def _DBMS_IDENTITY
return "DEFAULT 'auto-identity'"
end

def _DBMS_PRIMARY_KEY_TYPE
   return "VARCHAR2(15) #{_DBMS_IDENTITY} NOT NULL PRIMARY KEY"
end

def _DBMS_VARCHAR(len)
return "VARCHAR2(#{len} CHAR)"
end

def _DBMS_DATETIME
db_platform_with_version="oracle9"
	if db_platform_with_version=="oracle9" 
		return "TIMESTAMP"
	elsif db_platform_with_version=="oracle10" 
		return "TIMESTAMP"
	else
		return "DATE"
	end
end

def _DBMS_TEXT
return "VARCHAR2(4000)"
end

def _DBMS_VARCHAR(len)
return "VARCHAR2(#{len} CHAR)"
end


def _new_query(table,name,query,roles)
str=<<END_OF_STR
 INSERT INTO queries( ID, table_name, name, query )
  VALUES( beIDGenerator.NEXTVAL, '#{table}','#{name}'

INSERT INTO queries_per_role( queryID, ID, role_name )
 SELECT beIDGenerator.CURRVAL, 'a' || SUBSTR(r.role_name, 1, 14), r.role_name FROM roles r WHERE r.role_name IN ( #{roles} );
END_OF_STR
puts str
end 

def _DBMS_DATE_FORMAT(param)
return "TO_CHAR(#{param},''YYYY-MM-DD'')"
end

ROLE_NONE="'empty roles list'"















query=<<END_OF_STR
<if parameter="_tcloneid_">
    '<parameter:_tcloneid_ default=""/>' AS "___tcloneid",
</if>
   PERSON_NAME(p) AS "ФИО",
   #{_DBMS_DATE_FORMAT("p.birthday")} AS "Birthday",
   _DBMS_CONCAT( 'persons.', _DBMS_CAST_INT_TO_VARCHAR(p.ID) ) AS "___ownerID",
   'persons' AS "___ent",
   'Click' AS "СПГ",
<if parameter="_tcloneid_">
   '<nosort/>;<link using="___ownerID,___ent,___tcloneid" table="attributeValues" columns="ownerID,___mainEntity,_tcloneid_"/>' AS ";СПГ",
</if>
END_OF_STR

_new_query( "persons", "PASSPORTS_FIELDS_INCLUDE_FIO",
query,
ROLE_NONE)

_drop_if_exists_and_create_table("processes",
        "ID #{_DBMS_PRIMARY_KEY_TYPE},
        name     #{_DBMS_VARCHAR(255)} NOT NULL,
        #{_quote('start')}   #{_DBMS_DATETIME} NOT NULL,
        #{_quote('end')}     #{_DBMS_DATETIME},
        type     #{_DBMS_VARCHAR(32)},
        state    #{_DBMS_VARCHAR(32)},
        message  #{_DBMS_TEXT},
        progress #{_DBMS_VARCHAR(32)},
        errors   #{_DBMS_TEXT}"
);








