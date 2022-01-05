package cs236351.multipaxos

import com.google.protobuf.Empty
import cs236351.multipaxos.MultiPaxosAcceptorServiceGrpc.getServiceDescriptor
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Holder for Kotlin coroutine-based client and server APIs for
 * cs236351.multipaxos.MultiPaxosAcceptorService.
 */
object MultiPaxosAcceptorServiceGrpcKt {
  const val SERVICE_NAME: String = MultiPaxosAcceptorServiceGrpc.SERVICE_NAME

  @JvmStatic
  val serviceDescriptor: ServiceDescriptor
    get() = MultiPaxosAcceptorServiceGrpc.getServiceDescriptor()

  val doPrepareMethod: MethodDescriptor<Prepare, Promise>
    @JvmStatic
    get() = MultiPaxosAcceptorServiceGrpc.getDoPrepareMethod()

  val doAcceptMethod: MethodDescriptor<Accept, Accepted>
    @JvmStatic
    get() = MultiPaxosAcceptorServiceGrpc.getDoAcceptMethod()

  /**
   * A stub for issuing RPCs to a(n) cs236351.multipaxos.MultiPaxosAcceptorService service as
   * suspending coroutines.
   */
  @StubFor(MultiPaxosAcceptorServiceGrpc::class)
  class MultiPaxosAcceptorServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT
  ) : AbstractCoroutineStub<MultiPaxosAcceptorServiceCoroutineStub>(channel, callOptions) {
    override fun build(channel: Channel, callOptions: CallOptions):
        MultiPaxosAcceptorServiceCoroutineStub = MultiPaxosAcceptorServiceCoroutineStub(channel,
        callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun doPrepare(request: Prepare, headers: Metadata = Metadata()): Promise = unaryRpc(
      channel,
      MultiPaxosAcceptorServiceGrpc.getDoPrepareMethod(),
      request,
      callOptions,
      headers
    )
    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun doAccept(request: Accept, headers: Metadata = Metadata()): Accepted = unaryRpc(
      channel,
      MultiPaxosAcceptorServiceGrpc.getDoAcceptMethod(),
      request,
      callOptions,
      headers
    )}

  /**
   * Skeletal implementation of the cs236351.multipaxos.MultiPaxosAcceptorService service based on
   * Kotlin coroutines.
   */
  abstract class MultiPaxosAcceptorServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for cs236351.multipaxos.MultiPaxosAcceptorService.DoPrepare.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun doPrepare(request: Prepare): Promise = throw
        StatusException(UNIMPLEMENTED.withDescription("Method cs236351.multipaxos.MultiPaxosAcceptorService.DoPrepare is unimplemented"))

    /**
     * Returns the response to an RPC for cs236351.multipaxos.MultiPaxosAcceptorService.DoAccept.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun doAccept(request: Accept): Accepted = throw
        StatusException(UNIMPLEMENTED.withDescription("Method cs236351.multipaxos.MultiPaxosAcceptorService.DoAccept is unimplemented"))

    final override fun bindService(): ServerServiceDefinition = builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = MultiPaxosAcceptorServiceGrpc.getDoPrepareMethod(),
      implementation = ::doPrepare
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = MultiPaxosAcceptorServiceGrpc.getDoAcceptMethod(),
      implementation = ::doAccept
    )).build()
  }
}

/**
 * Holder for Kotlin coroutine-based client and server APIs for
 * cs236351.multipaxos.MultiPaxosLearnerService.
 */
object MultiPaxosLearnerServiceGrpcKt {
  const val SERVICE_NAME: String = MultiPaxosLearnerServiceGrpc.SERVICE_NAME

  @JvmStatic
  val serviceDescriptor: ServiceDescriptor
    get() = MultiPaxosLearnerServiceGrpc.getServiceDescriptor()

  val doCommitMethod: MethodDescriptor<Commit, Empty>
    @JvmStatic
    get() = MultiPaxosLearnerServiceGrpc.getDoCommitMethod()

  /**
   * A stub for issuing RPCs to a(n) cs236351.multipaxos.MultiPaxosLearnerService service as
   * suspending coroutines.
   */
  @StubFor(MultiPaxosLearnerServiceGrpc::class)
  class MultiPaxosLearnerServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT
  ) : AbstractCoroutineStub<MultiPaxosLearnerServiceCoroutineStub>(channel, callOptions) {
    override fun build(channel: Channel, callOptions: CallOptions):
        MultiPaxosLearnerServiceCoroutineStub = MultiPaxosLearnerServiceCoroutineStub(channel,
        callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    suspend fun doCommit(request: Commit, headers: Metadata = Metadata()): Empty = unaryRpc(
      channel,
      MultiPaxosLearnerServiceGrpc.getDoCommitMethod(),
      request,
      callOptions,
      headers
    )}

  /**
   * Skeletal implementation of the cs236351.multipaxos.MultiPaxosLearnerService service based on
   * Kotlin coroutines.
   */
  abstract class MultiPaxosLearnerServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for cs236351.multipaxos.MultiPaxosLearnerService.DoCommit.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    open suspend fun doCommit(request: Commit): Empty = throw
        StatusException(UNIMPLEMENTED.withDescription("Method cs236351.multipaxos.MultiPaxosLearnerService.DoCommit is unimplemented"))

    final override fun bindService(): ServerServiceDefinition =
        builder(MultiPaxosLearnerServiceGrpc.getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = MultiPaxosLearnerServiceGrpc.getDoCommitMethod(),
      implementation = ::doCommit
    )).build()
  }
}
