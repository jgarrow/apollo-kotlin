package com.apollographql.apollo3.compiler.codegen.file

import com.apollographql.apollo3.compiler.codegen.CgContext
import com.apollographql.apollo3.compiler.codegen.CgFile
import com.apollographql.apollo3.compiler.codegen.CgFileBuilder
import com.apollographql.apollo3.compiler.codegen.compiledfield.CompiledFieldsBuilder
import com.apollographql.apollo3.compiler.ir.IrOperation
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec

class OperationResponseFieldsBuilder(
    val context: CgContext,
    val operation: IrOperation,
) : CgFileBuilder {
  private val packageName = context.layout.operationResponseFieldsPackageName(operation.filePath)
  private val simpleName = context.layout.operationResponseFieldsName(operation)

  private val responseFieldsBuilder = CompiledFieldsBuilder(
      rootCompiledField = operation.compiledField,
      rootName = simpleName
  )

  override fun prepare() {
    context.resolver.registerOperationMergedFields(
        operation.name,
        ClassName(packageName, simpleName)
    )
  }

  override fun build(): CgFile {
    return CgFile(
        packageName = packageName,
        fileName = simpleName,
        typeSpecs = listOf(typeSpec())
    )
  }

  private fun typeSpec(): TypeSpec {
    return responseFieldsBuilder.build()
  }
}