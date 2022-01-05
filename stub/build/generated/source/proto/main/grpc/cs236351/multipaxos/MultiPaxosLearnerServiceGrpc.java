package cs236351.multipaxos;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: multipaxos/multipaxos.proto")
public final class MultiPaxosLearnerServiceGrpc {

  private MultiPaxosLearnerServiceGrpc() {}

  public static final String SERVICE_NAME = "cs236351.multipaxos.MultiPaxosLearnerService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<cs236351.multipaxos.Commit,
      com.google.protobuf.Empty> getDoCommitMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DoCommit",
      requestType = cs236351.multipaxos.Commit.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cs236351.multipaxos.Commit,
      com.google.protobuf.Empty> getDoCommitMethod() {
    io.grpc.MethodDescriptor<cs236351.multipaxos.Commit, com.google.protobuf.Empty> getDoCommitMethod;
    if ((getDoCommitMethod = MultiPaxosLearnerServiceGrpc.getDoCommitMethod) == null) {
      synchronized (MultiPaxosLearnerServiceGrpc.class) {
        if ((getDoCommitMethod = MultiPaxosLearnerServiceGrpc.getDoCommitMethod) == null) {
          MultiPaxosLearnerServiceGrpc.getDoCommitMethod = getDoCommitMethod =
              io.grpc.MethodDescriptor.<cs236351.multipaxos.Commit, com.google.protobuf.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DoCommit"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cs236351.multipaxos.Commit.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new MultiPaxosLearnerServiceMethodDescriptorSupplier("DoCommit"))
              .build();
        }
      }
    }
    return getDoCommitMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MultiPaxosLearnerServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MultiPaxosLearnerServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MultiPaxosLearnerServiceStub>() {
        @java.lang.Override
        public MultiPaxosLearnerServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MultiPaxosLearnerServiceStub(channel, callOptions);
        }
      };
    return MultiPaxosLearnerServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MultiPaxosLearnerServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MultiPaxosLearnerServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MultiPaxosLearnerServiceBlockingStub>() {
        @java.lang.Override
        public MultiPaxosLearnerServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MultiPaxosLearnerServiceBlockingStub(channel, callOptions);
        }
      };
    return MultiPaxosLearnerServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MultiPaxosLearnerServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MultiPaxosLearnerServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MultiPaxosLearnerServiceFutureStub>() {
        @java.lang.Override
        public MultiPaxosLearnerServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MultiPaxosLearnerServiceFutureStub(channel, callOptions);
        }
      };
    return MultiPaxosLearnerServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class MultiPaxosLearnerServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void doCommit(cs236351.multipaxos.Commit request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDoCommitMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getDoCommitMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                cs236351.multipaxos.Commit,
                com.google.protobuf.Empty>(
                  this, METHODID_DO_COMMIT)))
          .build();
    }
  }

  /**
   */
  public static final class MultiPaxosLearnerServiceStub extends io.grpc.stub.AbstractAsyncStub<MultiPaxosLearnerServiceStub> {
    private MultiPaxosLearnerServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MultiPaxosLearnerServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MultiPaxosLearnerServiceStub(channel, callOptions);
    }

    /**
     */
    public void doCommit(cs236351.multipaxos.Commit request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDoCommitMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MultiPaxosLearnerServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<MultiPaxosLearnerServiceBlockingStub> {
    private MultiPaxosLearnerServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MultiPaxosLearnerServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MultiPaxosLearnerServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty doCommit(cs236351.multipaxos.Commit request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDoCommitMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MultiPaxosLearnerServiceFutureStub extends io.grpc.stub.AbstractFutureStub<MultiPaxosLearnerServiceFutureStub> {
    private MultiPaxosLearnerServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MultiPaxosLearnerServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MultiPaxosLearnerServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> doCommit(
        cs236351.multipaxos.Commit request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDoCommitMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DO_COMMIT = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MultiPaxosLearnerServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MultiPaxosLearnerServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DO_COMMIT:
          serviceImpl.doCommit((cs236351.multipaxos.Commit) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class MultiPaxosLearnerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MultiPaxosLearnerServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return cs236351.multipaxos.Multipaxos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MultiPaxosLearnerService");
    }
  }

  private static final class MultiPaxosLearnerServiceFileDescriptorSupplier
      extends MultiPaxosLearnerServiceBaseDescriptorSupplier {
    MultiPaxosLearnerServiceFileDescriptorSupplier() {}
  }

  private static final class MultiPaxosLearnerServiceMethodDescriptorSupplier
      extends MultiPaxosLearnerServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MultiPaxosLearnerServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MultiPaxosLearnerServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MultiPaxosLearnerServiceFileDescriptorSupplier())
              .addMethod(getDoCommitMethod())
              .build();
        }
      }
    }
    return result;
  }
}
