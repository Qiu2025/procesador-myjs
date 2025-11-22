<eof,> no lo consideramos token al principio, pero después lo añadimos por un error que nos provocaba al analizador sintáctico
(cuando había un \n en la entrada el léxico devuelve un token "" vacío y el sintáctico al no encontrar $ da error, cuando $ estaba
detrás del \n)