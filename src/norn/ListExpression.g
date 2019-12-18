// Grammar for Norn mailing list expressions

@skip whitespace {
    expression ::= parallel;
    parallel ::= sequence ('|' sequence)*;
    sequence ::= (listdefinition | union) (';' (listdefinition | union))*;
    listdefinition ::= mailingList '=' ((union) | listdefinition)*;
    union ::= difference (',' difference)*;
    difference ::= intersection ('!' intersection)*;
    intersection ::= primitive ('*' primitive)*;
    primitive ::= email | mailingList | '(' expression ')';
}

email ::= [a-zA-Z0-9_./-/+]+ [@] [a-zA-Z0-9_./-/]+;
mailingList ::= [a-zA-Z0-9_.-]+;
whitespace ::= [ \t\r\n]+;
