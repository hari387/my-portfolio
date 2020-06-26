---
layout: page 
title: Projects

---

# Projects
---

### Ray Tracing

A friend and I built a ray tracer in pure C++, without any graphics libraries. We also gave it the functionality to raytrace `.stl` files, and sped it up 3x by using a [Bounding Volume Hierachy](https://en.wikipedia.org/wiki/Bounding_volume_hierarchy). You can find the project [here](https://github.com/hari387/architectural_raytracer). It's possible to speed it up even more using CUDA, but we decided to optimize for developer time.

![Raytraced Spheres](https://camo.githubusercontent.com/f33e693d485971d62937a83be95586c5570493b5/68747470733a2f2f692e696d6775722e636f6d2f41675a554e496e2e706e67)

If you'd like to do this yourself, [Ray Tracing in One Weekend](https://github.com/RayTracing/raytracing.github.io) by Peter Shirley is an excellent resource.

---

### Neural Network _sans_ Tensorflow

I built a Neural Network class in Python without any machine learning frameworks and trained a network on the MNIST dataset up to a 90% accuracy. In the process, I learned a bunch about optimization, multivariable calculus, and more. You can access the project and run it [here](https://repl.it/@mpm3/mllearning#network.py).

![](../assets/images/neuralnet.png)

Again, if you'd like to learn more about this, [Neural Networks and Deep Learning](http://neuralnetworksanddeeplearning.com/) by Michael Nielsen is a great book.

---

### Gridsnek<span>.io</span>
I built a fully functional grid version of [slither.io](slither.io) in the browser using [phaser.io](phaser.io), a game engine made for the browser, and [socket.io](socket.io). You can find the project [here](https://github.com/hari387/ekans.io), or play it [here](ekans-io.herokuapp.com).

![](../assets/images/gridsnek.png)