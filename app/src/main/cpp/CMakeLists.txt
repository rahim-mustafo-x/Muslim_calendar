cmake_minimum_required(VERSION 3.22.1)
project("muslimcalendar")

add_library(
        native-lib
        SHARED
        native-lib.cpp   # your .cpp file name here
)

find_library(log-lib log)

target_link_libraries(
        native-lib
        ${log-lib}
)