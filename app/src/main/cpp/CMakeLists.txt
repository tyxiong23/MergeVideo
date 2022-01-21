# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("myapplication")

include_directories(
        ffmpeg/include
)
include_directories(
       opencv/include
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        myapplication

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp)

add_library(
        finetune-lib
        SHARED
        finetune.cpp
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

set(prebuild ${CMAKE_SOURCE_DIR}/ffmpeg/prebuild/arm64-v8a)
set(opencv_lib ${CMAKE_SOURCE_DIR}/opencv/lib)

add_library(libopencv_java3
        SHARED
        IMPORTED
)

SET_TARGET_PROPERTIES(
        libopencv_java3
        PROPERTIES IMPORTED_LOCATION
        ${opencv_lib}/libopencv_java3.so
)

add_library(libavcodec
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libavcodec
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libavcodec.so
)

#add libavfilter
add_library(libavfilter
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libavfilter
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libavfilter.so
)


#add libavformat
add_library(libavformat
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libavformat
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libavformat.so
)


#add libavutil
add_library(libavutil
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libavutil
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libavutil.so
)


#add libpostproc
add_library(libpostproc
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libpostproc
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libpostproc.so
)

#add libswresample
add_library(libswresample
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libswresample
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libswresample.so
)

#add libswscale
add_library(libswscale
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libswscale
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libswscale.so
)

add_library(libffmpeg-invoke
        SHARED
        IMPORTED
        )

SET_TARGET_PROPERTIES(
        libffmpeg-invoke
        PROPERTIES IMPORTED_LOCATION
        ${prebuild}/libffmpeg-invoke.so
)

set(third-party-libs
        libavcodec
        libavfilter
        libavformat
        libavutil
        libffmpeg-invoke
        libpostproc
        libswresample
        libswscale
        libopencv_java3
        )



# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
target_link_libraries(
        finetune-lib
        libopencv_java3
)


target_link_libraries( # Specifies the target library.
        myapplication
        finetune-lib
        ${third-party-libs}

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

