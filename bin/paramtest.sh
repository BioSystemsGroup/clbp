#!/bin/bash
java -cp ".:dist/clbp.jar"$(unset cp;for i in lib/*.jar; do cp=$cp":"$i; done; echo $cp) clbp.ctrl.Parameters cfg/parameters.json
exit

