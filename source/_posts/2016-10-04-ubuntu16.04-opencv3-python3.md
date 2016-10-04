---
title: Ubuntu16.04上编译OpenCV的Python3包
comments: true
toc: true
date: 2016-10-04 13:41:02
categories: MachineLearning
tags : OpenCV
keywords: Ubuntu, OpenCV, Python3
---

>**本节内容：**本篇博文记录在Ubuntu16.04上安装OpenCV。

<!-- more -->

## 安装依赖软件

从源代码编译时需要很多第三方软件，因此首先需要在系统中安装这些库，具体如下:

``` python
sudo apt-get install build-essential cmake cmake-gui git libjpeg-dev libpng-dev       \
libtiff5-dev libjasper-dev libavcodec-dev libavformat-dev libswscale-dev pkg-config   \
libgtk2.0-dev libeigen3-dev libtheora-dev libvorbis-dev libxvidcore-dev libx264-dev   \
sphinx-common libtbb-dev yasm libfaac-dev libopencore-amrnb-dev libopencore-amrwb-dev \
libopenexr-dev libgstreamer-plugins-base1.0-dev libavcodec-dev libavutil-dev \
libavfilter-dev libavformat-dev libavresample-dev libgphoto2-dev libhdf5-mpi-dev \
libhdf5-openmpi-dev libhdf5-mpich-dev
```

安装好这些库后便可以从`github`上`clone`最新的代码:

``` shell
mkdir build && cd build
git clone --branch 3.1.0 --depth 1 https://github.com/Itseez/opencv.git
git clone --branch 3.1.0 --depth 1 https://github.com/itseez/opencv_contrib
```

建立编译目录：

``` shell
cd opencv && mkdir release
cd release
```

## 编译

接上，若使用Anaconda Python，则指定Python库所在的位置，这里是`/usr/local/anaconda3/lib/libpython3.5m.so`：在终端下运行：

``` python
cmake -D OPENCV_EXTRA_MODULES_PATH=../../opencv_contrib/modules/  -DBUILD_TIFF=ON \
-DBUILD_opencv_java=ON -DWITH_CUDA=OFF -DENABLE_AVX=ON -DWITH_OPENGL=ON -DWITH_OPENCL=ON \
-DWITH_IPP=ON -DWITH_TBB=ON -DWITH_EIGEN=ON -DWITH_V4L=ON -DBUILD_TESTS=OFF \
-DBUILD_PERF_TESTS=OFF -DCMAKE_BUILD_TYPE=RELEASE -DCMAKE_INSTALL_PREFIX=$(python3 -c "import sys; print(sys.prefix)") \
-DPYTHON_EXECUTABLE=$(which python3) \
-DPYTHON_INCLUDE_DIR=$(python3 -c "from distutils.sysconfig import get_python_inc; print(get_python_inc())") \
-DPYTHON_PACKAGES_PATH=$(python3 -c "from distutils.sysconfig import get_python_lib; print(get_python_lib())") \
-DPYTHON_LIBRARY=/usr/local/anaconda3/lib/libpython3.5m.so ..
```

没有报错后，接着便`make`和`make install` :

``` python
make 
sudo make install
```

## 使用

安装完成后，测试是否安装成功:

``` python
import cv2 
x = cv2.bgsegm.createBackgroundSubtractorGMG() 
```
