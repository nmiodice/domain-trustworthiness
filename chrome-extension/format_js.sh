BEAUTIFY_CMD="js-beautify"
BEAUTIFY_OPS="-n"

for JS_FILE in $(ls app/*.js)
do
    if [[ $JS_FILE != *".min.js"* ]]; then
        echo $JS_FILE
        JS=$($BEAUTIFY_CMD $BEAUTIFY_OPS $JS_FILE)
        echo "$JS" > $JS_FILE
    fi
done