#!/bin/sh
cd examples/gradle_example
./build_and_run.sh
rm -rf target
cd ../../examples/maven_example
./build_and_run.sh
rm -rf target
cd ../../examples/kotlin_example
./build_and_run.sh
rm -rf target
cd ../../examples/append_to_pdf
./build_and_run.sh
rm -rf target
cd ../../examples/pdfbox3
./build_and_run.sh
rm -rf target
cd ../../examples/jasper_reports_rendering
./build_and_run.sh
rm -rf target
