package cs236351.multipaxos;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.39.0)",
    comments = "Source: multipaxos/multipaxos.proto")
public final class MultiPaxosAcceptorServiceGrpc {

  private MultiPaxosAcceptorServiceGrpc() {}

  public static final String SERVICE_NAME = "cs236351.multipaxos.MultiPaxosAcceptorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<cs236351.multipaxos.Prepare,
      cs236351.multipaxos.Promise> getDoPrepareMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DoPrepare",
      requestType = cs236351.multipaxos.Prepare.class,
      responseType = cs236351.multipaxos.Promise.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cs236351.multipaxos.Prepare,
      cs236351.multipaxos.Promise> getDoPrepareMethod() {
    io.grpc.MethodDescriptor<cs236351.multipaxos.Prepare, cs236351.multipaxos.Promise> getDoPrepareMethod;
    if ((getDoPrepareMethod = MultiPaxosAcceptorServiceGrpc.getDoPrepareMethod) == null) {
      synchronized (MultiPaxosAcceptorServiceGrpc.class) {
        if ((getDoPrepareMethod = MultiPaxosAcceptorServiceGrpc.getDoPrepareMethod) == null) {
          MultiPaxosAcceptorServiceGrpc.getDoPrepareMethod = getDoPrepareMethod =
              io.grpc.MethodDescriptor.<cs236351.multipaxos.Prepare, cs236351.multipaxos.Promise>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DoPrepare"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cs236351.multipaxos.Prepare.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cs236351.multipaxos.Promise.getDefaultInstance()))
              .setSchemaDescriptor(new MultiPaxosAcceptorServiceMethodDescriptorSupplier("DoPrepare"))
              .build();
        }
      }
    }
    return getDoPrepareMethod;
  }

  private static volatile io.grpc.MethodDescriptor<cs236351.multipaxos.Accept,
      cs236351.multipaxos.Accepted> getDoAcceptMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DoAccept",
      requestType = cs236351.multipaxos.Accept.class,
      responseType = cs236351.multipaxos.Accepted.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cs236351.multipaxos.Accept,
      cs236351.multipaxos.Accepted> getDoAcceptMethod() {
    io.grpc.MethodDescriptor<cs236351.multipaxos.Accept, cs236351.multipaxos.Accepted> getDoAcceptMethod;
    if ((getDoAcceptMethod = MultiPaxosAcceptorServiceGrpc.getDoAcceptMethod) == null) {
      synchronized (MultiPaxosAcceptorServiceGrpc.class) {
        if ((getDoAcceptMethod = MultiPaxosAcceptorServiceGrpc.getDoAcceptMethod) == null) {
          MultiPaxosAcceptorServiceGrpc.getDoAcceptMethod = getDoAcceptMethod =
              io.grpc.MethodDescriptor.<cs236351.multipaxos.Accept, cs236351.multipaxos.Accepted>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DoAccept"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cs236351.multipaxos.Accept.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  cs236351.multipaxos.Accepted.getDefaultInstance()))
              .setSchemaDescriptor(new MultiPaxosAcceptorServiceMethodDescriptorSupplier("DoAccept"))
              .build();
        }
      }
    }
    return getDoAcceptMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MultiPaxosAcceptorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MultiPaxosAcceptorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MultiPaxosAcceptorServiceStub>() {
        @java.lang.Override
        public MultiPaxosAcceptorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MultiPaxosAcceptorServiceStub(channel, callOptions);
        }
      };
    return MultiPaxosAcceptorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MultiPaxosAcceptorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MultiPaxosAcceptorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MultiPaxosAcceptorServiceBlockingStub>() {
        @java.lang.Override
        public MultiPaxosAcceptorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MultiPaxosAcceptorServiceBlockingStub(channel, callOptions);
        }
      };
    return MultiPaxosAcceptorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MultiPaxosAcceptorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MultiPaxosAcceptorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MultiPaxosAcceptorServiceFutureStub>() {
        @java.lang.Override
        public MultiPaxosAcceptorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MultiPaxosAcceptorServiceFutureStub(channel, callOptions);
        }
      };
    return MultiPaxosAcceptorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class MultiPaxosAcceptorServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void doPrepare(cs236351.multipaxos.Prepare request,
        io.grpc.stub.StreamObserver<cs236351.multipaxos.Promise> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDoPrepareMethod(), responseObserver);
    }

    /**
     */
    public void doAccept(cs236351.multipaxos.Accept request,
        io.grpc.stub.StreamObserver<cs236351.multipaxos.Accepted> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDoAcceptMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getDoPrepareMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                cs236351.multipaxos.Prepare,
                cs236351.multipaxos.Promise>(
                  this, METHODID_DO_PREPARE)))
          .addMethod(
            getDoAcceptMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                cs236351.multipaxos.Accept,
                cs236351.multipaxos.Accepted>(
                  this, METHODID_DO_ACCEPT)))
          .build();
    }
  }

  /**
   */
  public static final class MultiPaxosAcceptorServiceStub extends io.grpc.stub.AbstractAsyncStub<MultiPaxosAcceptorServiceStub> {
    private MultiPaxosAcceptorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MultiPaxosAcceptorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MultiPaxosAcceptorServiceStub(channel, callOptions);
    }

    /**
     */
    public void doPrepare(cs236351.multipaxos.Prepare request,
        io.grpc.stub.StreamObserver<cs236351.multipaxos.Promise> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDoPrepareMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void doAccept(cs236351.multipaxos.Accept request,
        io.grpc.stub.StreamObserver<cs236351.multipaxos.Accepted> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDoAcceptMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MultiPaxosAcceptorServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<MultiPaxosAcceptorServiceBlockingStub> {
    private MultiPaxosAcceptorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MultiPaxosAcceptorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MultiPaxosAcceptorServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public cs236351.multipaxos.Promise doPrepare(cs236351.multipaxos.Prepare request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDoPrepareMethod(), getCallOptions(), request);
    }

    /**
     */
    public cs236351.multipaxos.Accepted doAccept(cs236351.multipaxos.Accept request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDoAcceptMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MultiPaxosAcceptorServiceFutureStub extends io.grpc.stub.AbstractFutureStub<MultiPaxosAcceptorServiceFutureStub> {
    private MultiPaxosAcceptorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MultiPaxosAcceptorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MultiPaxosAcceptorServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cs236351.multipaxos.Promise> doPrepare(
        cs236351.multipaxos.Prepare request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDoPrepareMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<cs236351.multipaxos.Accepted> doAccept(
        cs236351.multipaxos.Accept request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDoAcceptMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DO_PREPARE = 0;
  private static final int METHODID_DO_ACCEPT = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MultiPaxosAcceptorServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MultiPaxosAcceptorServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DO_PREPARE:
          serviceImpl.doPrepare((cs236351.multipaxos.Prepare) request,
              (io.grpc.stub.StreamObserver<cs236351.multipaxos.Promise>) responseObserver);
          break;
        case METHODID_DO_ACCEPT:
          serviceImpl.doAccept((cs236351.multipaxos.Accept) request,
              (io.grpc.stub.StreamObserver<cs236351.multipaxos.Accepted>) responseObserver);
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

  private static abstract class MultiPaxosAcceptorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MultiPaxosAcceptorServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return cs236351.multipaxos.Multipaxos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MultiPaxosAcceptorService");
    }
  }

  private static final class MultiPaxosAcceptorServiceFileDescriptorSupplier
      extends MultiPaxosAcceptorServiceBaseDescriptorSupplier {
    MultiPaxosAcceptorServiceFileDescriptorSupplier() {}
  }

  private static final class MultiPaxosAcceptorServiceMethodDescriptorSupplier
      extends MultiPaxosAcceptorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MultiPaxosAcceptorServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (MultiPaxosAcceptorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MultiPaxosAcceptorServiceFileDescriptorSupplier())
              .addMethod(getDoPrepareMethod())
              .addMethod(getDoAcceptMethod())
              .build();
        }
      }
    }
    return result;
  }
}
