load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_binary", "scala_test")

scala_binary(
    name = "bin",
    srcs = [
            "GreetingApplicationLoader.scala", 
            "GreetingModule.scala",
            "//conf:play-routes"],
    visibility = ["//visibility:public"],
    main_class = "play.core.server.ProdServerStart",
    deps = ["//:play", 
            "//:macwire", 
            "//app/controllers:controllers", 
            "//app/services:services"],
    resource_strip_prefix = package_name(),
    classpath_resources = ["//conf:app_conf"],
    jvm_flags = [
    "-Dhttp.port=9000",
    "-Dapplication.name=bin",
    ],
)


# scala_image(
#     name = "main-docker2",
#     srcs = [
#             "scala/Main.scala",
#             ],
#     base = "//:custom_java_base",
#     main_class = "Main",
#     deps = [":mylib"],
# )
