load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_binary", "scala_test")

scala_library(
    name = "models",
    srcs = glob(["*.scala"]),
    visibility = ["//visibility:public"],
    deps = ["//:play"],
)